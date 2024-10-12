package org.dflib.jdbc.connector.metadata.flavors;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class DbFlavorFactory {

    public static DbFlavor create(DataSource dataSource) {
        try (Connection c = dataSource.getConnection()) {

            DatabaseMetaData jdbcMd = c.getMetaData();
            return createFlavor(jdbcMd);

        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to DB or retrieving DB metadata");
        }
    }

    private static DbFlavor createFlavor(DatabaseMetaData metadata) throws SQLException {

        String dbName = metadata.getDatabaseProductName();
        if (dbName == null) {
            return GenericFlavor.create(metadata);
        }

        // more string matches are available inside Apache Cayenne DB sniffers
        String dbNameUpper = dbName.toUpperCase();
        if (dbNameUpper.contains("MYSQL")) {
            return MySQLFlavor.create(metadata);
        } else if (dbNameUpper.contains("MARIADB")) {
            return MySQLFlavor.create(metadata);
        } else if (dbNameUpper.contains("APACHE DERBY")) {
            return DerbyFlavor.create(metadata);
        } else if (dbNameUpper.contains("POSTGRESQL")) {
            return PostgresFlavor.create(metadata);
        }

        return GenericFlavor.create(metadata);
    }
}
