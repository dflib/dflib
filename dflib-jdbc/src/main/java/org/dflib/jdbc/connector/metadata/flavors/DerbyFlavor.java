package org.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class DerbyFlavor extends GenericFlavor {

    public DerbyFlavor() {
    }

    public static DerbyFlavor create(DatabaseMetaData metaData) throws SQLException {
        DerbyFlavor flavor = new DerbyFlavor();
        flavor.supportsCatalogs = false;
        flavor.supportsSchemas = true;
        flavor.supportsParamsMetadata = true;
        flavor.supportsBatchUpdates = metaData.supportsBatchUpdates();
        flavor.identifierQuote = metaData.getIdentifierQuoteString();

        return flavor;
    }
}
