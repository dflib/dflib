package org.dflib.jdbc;

import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.JdbcConnectorBuilder;

import javax.sql.DataSource;

/**
 * An entry point to JDBC access functionality. Allows callers to build a {@link JdbcConnector} that can be reused for
 * loading or saving DataFrames from/to a database.
 */
public class Jdbc {

    /**
     * @return a new JdbcConnectorBuilder
     */
    public static JdbcConnectorBuilder connector() {
        return new JdbcConnectorBuilder();
    }

    public static JdbcConnectorBuilder connector(String dbUrl) {
        return new JdbcConnectorBuilder().url(dbUrl);
    }

    public static JdbcConnector connector(DataSource ds) {
        return connector().dataSource(ds).build();
    }
}
