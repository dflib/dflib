package org.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Provides DB type specific metadata and strategies for interacting with the database.
 */
public interface DbFlavor {

    /**
     * Detects the database type from the metadata.
     *
     * @since 2.0.0
     */
    static DbFlavor of(DatabaseMetaData metadata) throws SQLException {
        String dbName = metadata.getDatabaseProductName();
        if (dbName == null) {
            return GenericFlavor.of(metadata);
        }

        // more string matches are available inside Apache Cayenne DB sniffers
        String dbNameUpper = dbName.toUpperCase();
        if (dbNameUpper.contains("MYSQL") || dbNameUpper.contains("MARIADB")) {
            return MySQLFlavor.of(metadata);
        } else if (dbNameUpper.contains("APACHE DERBY")) {
            return DerbyFlavor.of(metadata);
        } else if (dbNameUpper.contains("POSTGRESQL")) {
            return PostgresFlavor.of(metadata);
        }

        return GenericFlavor.of(metadata);
    }

    String getIdentifierQuote();

    boolean supportsParamsMetadata();

    boolean supportsBatchUpdates();

    boolean supportsCatalogs();

    boolean supportsSchemas();

    int columnType(int jdbcType, String nativeType);
}
