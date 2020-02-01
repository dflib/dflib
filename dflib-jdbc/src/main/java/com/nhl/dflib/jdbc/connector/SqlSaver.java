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

    /**
     * Saves DataFrame contents using the underlying SQL. The SQL is translated into a JDBC PreparedStatement, and
     * the DataFrame data is passed to the statement row by row, row values used as statement bindings.
     */
    public void save(DataFrame data) {
        LOGGER.debug("saving DataFrame data...");
        connector.createStatementBuilder(sql).bindBatch(data).update();
    }

    /**
     * Saves Series contents using the underlying SQL. The SQL is translated into a JDBC PreparedStatement, and
     * the Series values are bound as statement parameters.
     */
    public void save(Series<?> data) {
        LOGGER.debug("saving Series data...");
        connector.createStatementBuilder(sql).bind(data).update();
    }

    /**
     * Saves array contents using the underlying SQL. The SQL is translated into a JDBC PreparedStatement, and
     * the Series values are bound as statement parameters.
     *
     * @param data an array of data. Can be empty if the underlying SQL has no parameters.
     */
    public void save(Object... data) {
        LOGGER.debug("saving array data...");
        connector.createStatementBuilder(sql).bind(data).update();
    }
}
