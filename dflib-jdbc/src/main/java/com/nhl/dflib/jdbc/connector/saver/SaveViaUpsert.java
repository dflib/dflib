package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.TableLoader;
import com.nhl.dflib.join.JoinIndicator;
import com.nhl.dflib.row.RowProxy;

import java.sql.Connection;
import java.util.Arrays;

/**
 * @since 0.6
 */
public class SaveViaUpsert extends SaveViaInsert {

    // used as a column for join indicator. Semi-random to avoid conflicts with real column names
    private static final String INDICATOR_COLUMN = "dflib_ind_%$#86AcD3";

    private String[] keyColumns;

    public SaveViaUpsert(JdbcConnector connector, String tableName, String[] keyColumns) {
        super(connector, tableName);
        this.keyColumns = keyColumns;
    }

    @Override
    protected void doSave(Connection connection, DataFrame df) {

        DataFrame keyDf = df.selectColumns(Index.forLabels(keyColumns));

        DataFrame previouslySaved = new TableLoader(connector, tableName)
                .includeColumns(df.getColumnsIndex().getLabels())
                .eq(keyDf)
                .load();

        if (previouslySaved.height() == 0) {
            insert(connection, df);
            return;
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

        insert(connection, df.selectRows(insertIndex));
        update(connection, df.selectRows(updateIndex), previouslySaved);
    }

    protected void insert(Connection connection, DataFrame toSave) {
        super.doSave(connection, toSave);
    }

    protected void update(Connection connection, DataFrame toSave, DataFrame previouslySaved) {

        if (toSave.width() == keyColumns.length) {
            log("All DataFrame columns are key columns. Skipping update.");
            return;
        }

        // skip unchanged rows...

        // TODO: we should be able to do logical ops with BooleanSeries without having to resort to row operations. E.g.
        //  bs1.and(bs2).and(bs3); Op.and(bs1, bs2, bs3)

        DataFrame eqMatrix = toSave.eq(previouslySaved);
        BooleanSeries alteredRowsIndex = eqMatrix.mapColumnAsBoolean(this::someFalse);
        DataFrame toSaveWithChanges = toSave.selectRows(alteredRowsIndex);

        Index valueIndex = toSave.getColumnsIndex().dropLabels(keyColumns);
        String[] valueColumns = valueIndex.getLabels();

        // TODO: don't UPDATE full rows. Do targeted updates only for updated values.. We already have a "eqMatrix" above
        //  that denotes cells with changes

        // valueAndKeyIndex index is different from "df.getColumnsIndex()", as it has the order of columns matching
        // PreparedStatement parameters order
        Index valueAndKeyIndex = valueIndex.addLabels(keyColumns);
        DataFrame params = toSaveWithChanges.selectColumns(valueAndKeyIndex);

        connector.createStatementBuilder(createUpdateStatement(keyColumns, valueColumns))

                // use param descriptors from metadata, as (1) we can and (b) some DBs don't support real
                // metadata in PreparedStatements. See e.g. https://github.com/nhl/dflib/issues/49

                .paramDescriptors(fixedParams(valueAndKeyIndex))
                .bindBatch(params)
                .update(connection);
    }

    protected boolean someFalse(RowProxy booleanRow) {

        int len = booleanRow.getIndex().size();
        for (int i = 0; i < len; i++) {
            if (!(boolean) booleanRow.get(i)) {
                return true;
            }
        }

        return false;
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
                .append(connector.quoteIdentifier(tableName))
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
