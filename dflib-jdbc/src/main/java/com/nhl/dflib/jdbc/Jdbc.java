package com.nhl.dflib.jdbc;

import com.nhl.dflib.jdbc.connector.JdbcConnector;

import javax.sql.DataSource;

/**
 * An entry point to JDBC access functionality.
 */
public class Jdbc {

    public static JdbcConnectorBuilder connector(String dbUrl) {
        return new JdbcConnectorBuilder(dbUrl);
    }

    public static JdbcConnector connector(DataSource ds) {
        return new JdbcConnector(ds);
    }
}
