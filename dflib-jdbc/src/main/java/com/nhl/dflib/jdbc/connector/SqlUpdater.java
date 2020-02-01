package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

/**
 * An object to load DataFrame or Series data to DB using custom SQL. Instances of this class can be reused for
 * different sets of data.
 *
 * @since 0.8
 */
public class SqlUpdater {

    protected JdbcConnector connector;
    private String sql;

    public SqlUpdater(JdbcConnector connector, String sql) {
        this.connector = connector;
        this.sql = sql;
    }

    public void update(DataFrame batchParams) {
        connector.createStatementBuilder(sql).bindBatch(batchParams).update();
    }

    public void update(Series<?> params) {
        connector.createStatementBuilder(sql).bind(params).update();
    }

    public void update(Object... params) {
        connector.createStatementBuilder(sql).bind(params).update();
    }
}
