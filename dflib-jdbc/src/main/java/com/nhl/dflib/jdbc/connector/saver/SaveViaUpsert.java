package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.TableLoader;
import com.nhl.dflib.join.JoinIndicator;

import java.sql.Connection;

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

        DataFrame toUpdate = new TableLoader(connector, tableName)
                // TODO: include all columns from "df" if we want to track changes before doing update
                .includeColumns(keyColumns)
                .eq(keyDf)
                .load();

        if (toUpdate.height() == 0) {
            insert(connection, df);
            return;
        }

        DataFrame insertAndUpdate = df.leftJoin()
                .on(keyHasher())
                .indicatorColumn(INDICATOR_COLUMN)
                .with(toUpdate);

        Series<JoinIndicator> index = insertAndUpdate.getColumn(INDICATOR_COLUMN);
        IntSeries insertIndex = index.index(i -> i == JoinIndicator.left_only);
        IntSeries updateIndex = index.index(i -> i == JoinIndicator.both);

        // if unique key
        if (insertAndUpdate.height() == df.height()) {
            insert(connection, df.selectRows(insertIndex));
            update(connection, df.selectRows(updateIndex));
        }
        // hmm.. the key was not unique. TODO: complain?
        else {
            DataFrame normalInsertAndUpdate = insertAndUpdate.selectColumns(df.getColumnsIndex());
            insert(connection, normalInsertAndUpdate.selectRows(insertIndex));
            update(connection, normalInsertAndUpdate.selectRows(updateIndex));
        }
    }

    protected void insert(Connection connection, DataFrame df) {
        super.doSave(connection, df);
    }

    protected void update(Connection connection, DataFrame df) {

        if (df.width() == keyColumns.length) {
            log("All DataFrame columns are key columns. Skipping update.");
            return;
        }

        // TODO: Minimize DB load - (1) don't update rows that haven't changed (2) for the rest update only the columns
        //  with changed values

        Index valueIndex = df.getColumnsIndex().dropLabels(keyColumns);
        String[] valueColumns = valueIndex.getLabels();

        // this one is different from "df.getColumnsIndex()", as it has the order of columns matching PreparedStatement
        // parameters order
        Index valueAndKeyIndex = valueIndex.addLabels(keyColumns);
        DataFrame params = df.selectColumns(valueAndKeyIndex);

        connector.createStatementBuilder(createUpdateStatement(keyColumns, valueColumns))

                // use param descriptors from metadata, as (1) we can and (b) some DBs don't support real
                // metadata in PreparedStatements. See e.g. https://github.com/nhl/dflib/issues/49

                .paramDescriptors(fixedParams(valueAndKeyIndex))
                .bindBatch(params)
                .update(connection);
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

        String sqlString = sql.toString();
        logSql(sqlString);
        return sqlString;
    }

}
