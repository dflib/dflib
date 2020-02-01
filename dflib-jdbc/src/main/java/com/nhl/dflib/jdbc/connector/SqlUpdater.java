package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object to load DataFrame or Series data to DB using custom SQL. Instances of this class can be reused for
 * different sets of data.
 *
 * @since 0.8
 */
public class SqlUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlUpdater.class);

    protected JdbcConnector connector;
    private String sql;

    public SqlUpdater(JdbcConnector connector, String sql) {
        this.connector = connector;
        this.sql = sql;
    }

    public void update(DataFrame batchParams) {
        LOGGER.debug("storing DataFrame data...");
        connector.createStatementBuilder(sql).bindBatch(batchParams).update();
    }

    public void update(Series<?> params) {
        LOGGER.debug("storing Series data...");
        connector.createStatementBuilder(sql).bind(params).update();
    }

    public void update(Object... params) {
        LOGGER.debug("storing array data...");
        connector.createStatementBuilder(sql).bind(params).update();
    }
}
