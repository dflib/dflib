package org.dflib.jdbc.connector.metadata.flavors;

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

        // with "generateSimpleParameterMetadata=true" we are getting back parameters, but they are all VARCHAR,
        // so rather useless
        flavor.supportsParamsMetadata = false;

        flavor.supportsBatchUpdates = metaData.supportsBatchUpdates();
        flavor.identifierQuote = metaData.getIdentifierQuoteString();
        return flavor;
    }

}
