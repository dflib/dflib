package com.nhl.dflib.jdbc.connector.metadata;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

class DbMetadataFactory {

    static DbMetadata create(DataSource dataSource) {
        try (Connection c = dataSource.getConnection()) {

            DatabaseMetaData jdbcMd = c.getMetaData();
            String dbName = jdbcMd.getDatabaseProductName();
            return new DbMetadata(dataSource, inferType(dbName), jdbcMd);

        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to DB or retrieving DB metadata");
        }
    }

    private static DbFlavor inferType(String dbName) {
        if (dbName == null) {
            return DbFlavor.OTHER;
        }

        // more string matches are available inside Apache Cayenne DB sniffers

        String dbNameUpper = dbName.toUpperCase();

        if (dbNameUpper.contains("MYSQL")) {
            return DbFlavor.MYSQL;
        } else if (dbNameUpper.contains("MARIADB")) {
            return DbFlavor.MARIA_DB;
        } else if (dbNameUpper.contains("APACHE DERBY")) {
            return DbFlavor.DERBY;
        }

        return DbFlavor.OTHER;
    }
}
