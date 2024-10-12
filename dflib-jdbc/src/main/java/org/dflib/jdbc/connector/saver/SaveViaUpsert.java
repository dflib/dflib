package org.dflib.jdbc.connector.saver;

import org.dflib.jdbc.SaveOp;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.StatementBuilder;
import org.dflib.jdbc.connector.TableLoader;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.DataFrame;
import org.dflib.GroupBy;
import org.dflib.Hasher;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.join.JoinIndicator;
import org.dflib.row.RowProxy;
import org.dflib.series.SingleValueSeries;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.function.Supplier;

public class SaveViaUpsert extends TableSaveStrategy {

    // used as a column for join indicator. Semi-random to avoid conflicts with real column names
    private static final String INDICATOR_COLUMN = "dflib_ind_%$#86AcD3";
    private static final String DIFF_COLUMN = "dflib_dif_%4$#96Ac3";

    protected String[] keyColumns;

    public SaveViaUpsert(JdbcConnector connector, TableFQName tableName, String[] keyColumns, int batchSize) {
        super(connector, tableName, batchSize);
        this.keyColumns = keyColumns;
    }

    @Override
    protected Supplier<Series<SaveOp>> doInsertOrUpdate(JdbcConnector connector, DataFrame df) {
        DataFrame keyDf = keyValues(df);

        DataFrame previouslySaved = new TableLoader(connector, tableName)
                .cols(df.getColumnsIndex().toArray())
                .eq(keyDf)
                .load();

        if (previouslySaved.height() == 0) {
            doInsert(connector, df);
            return () -> new SingleValueSeries<>(SaveOp.insert, df.height());
        }

        DataFrame insertAndUpdate = df.leftJoin(previouslySaved)
                .on(keyHasher())
                .indicatorColumn(INDICATOR_COLUMN)
                .select();

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
            doInsert(connector, df.rows(insertIndex).select());
        }

        if (updateIndex.size() > 0) {

            // we can't use "previouslySaved" DF for calculating update deltas, as it is not in the same order as "df".
            // So instead recreate ordered version of "previouslySaved" by getting data from the join df "insertAndUpdate"

            Index mainColumns = df.getColumnsIndex();
            Index joinedIndex = insertAndUpdate.getColumnsIndex().selectRange(mainColumns.size(), mainColumns.size() * 2);

            DataFrame previouslySavedOrdered = insertAndUpdate
                    .cols(joinedIndex).select()
                    .cols().as(mainColumns.toArray());

            doUpdate(connector,
                    df.rows(updateIndex).select(),
                    previouslySavedOrdered.rows(updateIndex).select(),
                    infoTracker);
        }

        return infoTracker::getInfo;
    }

    protected DataFrame keyValues(DataFrame df) {
        return df.cols(keyColumns).select();
    }

    protected void doUpdate(JdbcConnector connector, DataFrame toSave, DataFrame previouslySaved, UpsertInfoTracker infoTracker) {

        int w = toSave.width();
        if (w == keyColumns.length) {
            log("All DataFrame columns are key columns. Skipping update.");
            return;
        }

        // partition data to save by updated value positions (using a BitSet), then skip unchanged rows, and generate
        // a batch UPDATE for each parameter pattern

        // TODO: speed up the equality test by excluding "keyColumns" from both sides

        // note that "toSave" and "previouslySaved" must be ordered by key for "eq" to be meaningful
        DataFrame eqMatrix = toSave.eq(previouslySaved).colsAppend(DIFF_COLUMN).merge(this::booleansAsBitSet);
        DataFrame toSaveClassified = toSave.colsAppend(DIFF_COLUMN).merge(eqMatrix.getColumn(DIFF_COLUMN));

        infoTracker.updatesCardinality(toSaveClassified.getColumn(DIFF_COLUMN));

        GroupBy byUpdatePattern = toSaveClassified.group(DIFF_COLUMN);
        for (Object o : byUpdatePattern.getGroupKeys()) {
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
                    updateColumns[j++] = toUpdate.getColumnsIndex().get(i);
                }
            }

            // reorder columns to start with updated values and end with keys to match PreparedStatement parameter ordering
            Index valueIndex = Index.of(updateColumns).selectExcept(keyColumns);
            Index valueAndKeyIndex = valueIndex.expand(keyColumns);

            StatementBuilder builder = connector.createStatementBuilder(createUpdateStatement(keyColumns, valueIndex.toArray()))

                    // use param descriptors from metadata, as (1) we can and (b) some DBs don't support real
                    // metadata in PreparedStatements. See e.g. https://github.com/dflib/dflib/issues/49

                    .paramDescriptors(fixedParams(valueAndKeyIndex))
                    .bindBatch(toUpdate.cols(valueAndKeyIndex).select());

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
        Hasher h = Hasher.of(keyColumns[0]);
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
