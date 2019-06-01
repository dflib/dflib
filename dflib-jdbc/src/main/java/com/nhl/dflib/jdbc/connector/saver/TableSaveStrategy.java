package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @since 0.6
 */
public abstract class TableSaveStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableSaveStrategy.class);

    protected JdbcConnector connector;
    protected String tableName;

    public TableSaveStrategy(JdbcConnector connector, String tableName) {
        this.connector = connector;
        this.tableName = tableName;
    }

    public void save(DataFrame df) {

        if (!shouldSave(df)) {
            return;
        }

        try (Connection c = connector.getConnection()) {
            doSave(c, df);
            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error storing data in DB", e);
        }
    }

    protected abstract boolean shouldSave(DataFrame df);

    protected abstract void doSave(Connection connection, DataFrame df);


    protected void log(String line, String... messageParams) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(line, messageParams);
        }
    }
}
