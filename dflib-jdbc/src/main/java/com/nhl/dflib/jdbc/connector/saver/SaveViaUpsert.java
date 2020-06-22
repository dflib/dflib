package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.*;
import com.nhl.dflib.jdbc.SaveOp;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.StatementBuilder;
import com.nhl.dflib.jdbc.connector.TableLoader;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.join.JoinIndicator;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.SingleValueSeries;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.function.Supplier;

/**
 * @since 0.6
 */
public class SaveViaUpsert extends SaveViaInsert {

    // used as a column for join indicator. Semi-random to avoid conflicts with real column names
    private static final String INDICATOR_COLUMN = "dflib_ind_%$#86AcD3";
    private static final String DIFF_COLUMN = "dflib_dif_%4$#96Ac3";


    private String[] keyColumns;

    public SaveViaUpsert(JdbcConnector connector, TableFQName tableName, String[] keyColumns) {
        super(connector, tableName);
        this.keyColumns = keyColumns;
    }

    @Override
    protected Supplier<Series<SaveOp>> doSave(JdbcConnector connector, DataFrame df) {

        DataFrame keyDf = df.selectColumns(Index.forLabels(keyColumns));

        DataFrame previouslySaved = new TableLoader(connector, tableName)
                .includeColumns(df.getColumnsIndex().getLabels())
                .eq(keyDf)
                .load();

        if (previouslySaved.height() == 0) {
            insert(connector, df);
            return () -> new SingleValueSeries<>(SaveOp.insert, df.height());
        }

        DataFrame insertAndUpdate = df.leftJoin()
                .on(keyHasher())
                .indicatorColumn(INDICATOR_COLUMN)
                .with(previouslySaved);

        Series<JoinIndicator> index = insertAndUpdate.getColumn(INDICATOR_COLUMN);
        IntSeries insertIndex = index.index(i -> i == JoinIndicator.left_only);
        IntSeries updateIndex = index.index(i -> i == JoinIndicator.both);

        int heightDelta = insertAndUpdate.height() - df.height();
        if (heightDelta > 0) {
            String message = String.format("Duplicate rows in the database table %s using key columns %s. Specify key columns that produce unique DB rows.",
                    tableName,
                    Arrays.toString(keyColumns));

            throw new IllegalStateException(message);
        }
        // this should be impossible as insertAndUpdate is a left join, so its size is at least the same as the left side DF...
        // Still keeping the check for sanity...
        else if (heightDelta < 0) {
            throw new IllegalStateException();
        }

        UpsertInfoTracker infoTracker = new UpsertInfoTracker(df.width(), df.height());
        infoTracker.insertAndUpdate(index);

        if (insertIndex.size() > 0) {
            insert(connector, df.selectRows(insertIndex));
        }

        if (updateIndex.size() > 0) {

            // we can't use "previouslySaved" DF for calculating update deltas, as it is not in the same order as "df".
            // So instead recreate ordered version of "previouslySaved" by getting data from the join df "insertAndUpdate"

            Index mainColumns = df.getColumnsIndex();
            Index joinedIndex = insertAndUpdate.getColumnsIndex().rangeOpenClosed(mainColumns.size(), mainColumns.size() * 2);

            DataFrame previouslySavedOrdered = insertAndUpdate
                    .selectColumns(joinedIndex)
                    .renameColumns(mainColumns.getLabels());

            update(connector, df.selectRows(updateIndex), previouslySavedOrdered.selectRows(updateIndex), infoTracker);
        }

        return infoTracker::getInfo;
    }

    protected void insert(JdbcConnector connector, DataFrame toSave) {
        super.doSave(connector, toSave);
    }

    protected void update(JdbcConnector connector, DataFrame toSave, DataFrame previouslySaved, UpsertInfoTracker infoTracker) {

        int w = toSave.width();
        if (w == keyColumns.length) {
            log("All DataFrame columns are key columns. Skipping update.");
            return;
        }

        // partition data to save by updated value positions (using a BitSet), then skip unchanged rows, and generate
        // a batch UPDATE for each parameter pattern

        // TODO: speed up equality test by excluding "keyColumns" from both sides

        // note that "toSave" and "previouslySaved" must be ordered by key for "eq" to be meaningful
        DataFrame eqMatrix = toSave.eq(previouslySaved);
        DataFrame toSaveClassified = toSave
                .addColumn(DIFF_COLUMN, eqMatrix.mapColumn(this::booleansAsBitSet));

        infoTracker.updatesCardinality(toSaveClassified.getColumn(DIFF_COLUMN));

        GroupBy byUpdatePattern = toSaveClassified.group(DIFF_COLUMN);
        for (Object o : byUpdatePattern.getGroups()) {
            BitSet bits = (BitSet) o;

            int cardinality = bits.cardinality();

            // no changes, skip group
            if (cardinality == w) {
                continue;
            }

            DataFrame toUpdate = byUpdatePattern.getGroup(bits);
            String[] updateColumns = new String[w - cardinality];
            for (int i = 0, j = 0; i < w; i++) {
                if (!bits.get(i)) {
                    updateColumns[j++] = toUpdate.getColumnsIndex().getLabel(i);
                }
            }

            // reorder columns to start with updated values and end with keys to match PreparedStatement parameter ordering
            Index valueIndex = Index.forLabels(updateColumns).dropLabels(keyColumns);
            Index valueAndKeyIndex = valueIndex.addLabels(keyColumns);

            StatementBuilder builder = connector.createStatementBuilder(createUpdateStatement(keyColumns, valueIndex.getLabels()))

                    // use param descriptors from metadata, as (1) we can and (b) some DBs don't support real
                    // metadata in PreparedStatements. See e.g. https://github.com/nhl/dflib/issues/49

                    .paramDescriptors(fixedParams(valueAndKeyIndex))
                    .bindBatch(toUpdate.selectColumns(valueAndKeyIndex));

            try (Connection c = connector.getConnection()) {
                builder.update(c);
            } catch (SQLException e) {
                throw new RuntimeException("Error closing DB connection", e);
            }
        }
    }

    protected BitSet booleansAsBitSet(RowProxy booleanRow) {
        int w = booleanRow.getIndex().size();
        BitSet s = new BitSet(w);

        for (int i = 0; i < w; i++) {
            if ((boolean) booleanRow.get(i)) {
                s.set(i);
            }
        }
        return s;
    }

    protected Hasher keyHasher() {
        Hasher h = Hasher.forColumn(keyColumns[0]);
        for (int i = 1; i < keyColumns.length; i++) {
            h = h.and(keyColumns[i]);
        }

        return h;
    }

    protected String createUpdateStatement(String[] conditionColumns, String[] valueColumns) {

        StringBuilder sql = new StringBuilder("update ")
                .append(connector.quoteTableName(tableName))
                .append(" set ")
                .append(connector.quoteIdentifier(valueColumns[0]))
                .append(" = ?");

        for (int i = 1; i < valueColumns.length; i++) {
            sql.append(", ").append(connector.quoteIdentifier(valueColumns[i])).append(" = ?");
        }

        sql.append(" where ")
                .append(connector.quoteIdentifier(conditionColumns[0]))
                .append(" = ?");

        for (int i = 1; i < conditionColumns.length; i++) {
            sql.append(" and ").append(connector.quoteIdentifier(conditionColumns[i])).append(" = ?");
        }

        return sql.toString();
    }

}
