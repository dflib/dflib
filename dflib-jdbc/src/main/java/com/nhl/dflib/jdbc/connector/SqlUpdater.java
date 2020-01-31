package com.nhl.dflib.jdbc.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A raw SQL API for handling DB updates with DataFrames and Series.
 *
 * @since 0.8
 */
public class SqlUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlUpdater.class);

    protected JdbcConnector connector;

    public SqlUpdater(JdbcConnector connector) {
        this.connector = connector;
    }
}
