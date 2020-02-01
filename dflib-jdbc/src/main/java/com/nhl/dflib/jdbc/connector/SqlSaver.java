package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores DataFrame or Series data in DB via custom SQL. Instances of this class can be reused for different sets of
 * data. This is a more customizable alternative to {@link TableSaver}.
 *
 * @since 0.8
 */
public class SqlSaver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSaver.class);

    protected JdbcConnector connector;
    private String sql;

    public SqlSaver(JdbcConnector connector, String sql) {
        this.connector = connector;
        this.sql = sql;
    }

    public void save(DataFrame batchParams) {
        LOGGER.debug("saving DataFrame data...");
        connector.createStatementBuilder(sql).bindBatch(batchParams).update();
    }

    public void save(Series<?> params) {
        LOGGER.debug("saving Series data...");
        connector.createStatementBuilder(sql).bind(params).update();
    }

    public void save(Object... params) {
        LOGGER.debug("saving array data...");
        connector.createStatementBuilder(sql).bind(params).update();
    }
}
