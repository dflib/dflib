package com.nhl.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @since 0.8
 */
public class MySQLFlavor extends GenericFlavor {

    protected MySQLFlavor() {
    }

    public static MySQLFlavor create(DatabaseMetaData metaData) throws SQLException {
        MySQLFlavor flavor = new MySQLFlavor();
        flavor.supportsCatalogs = true;
        flavor.supportsSchemas = false;
        // MySQL kinda supports params metadata, but that requires a special URL parameter in the driver
        flavor.supportsParamsMetadata = false;
        flavor.supportsBatchUpdates = metaData.supportsBatchUpdates();
        flavor.identifierQuote = metaData.getIdentifierQuoteString();
        return flavor;
    }

}
