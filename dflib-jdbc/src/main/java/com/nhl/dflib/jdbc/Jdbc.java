package com.nhl.dflib.jdbc;

import com.nhl.dflib.jdbc.connector.DefaultJdbcConnector;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.JdbcConnectorBuilder;
import com.nhl.dflib.jdbc.connector.metadata.DbMetadata;

import javax.sql.DataSource;

/**
 * An entry point to JDBC access functionality. Allows callers to build a {@link JdbcConnector} that can be reused for
 * loading one or more DataFrames from the database.
 */
public class Jdbc {

    public static JdbcConnectorBuilder connector(String dbUrl) {
        return new JdbcConnectorBuilder(dbUrl);
    }

    public static JdbcConnector connector(DataSource ds) {
        return new DefaultJdbcConnector(ds, DbMetadata.create(ds));
    }
}
