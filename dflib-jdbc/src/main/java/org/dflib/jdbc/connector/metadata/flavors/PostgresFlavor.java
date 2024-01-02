package org.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @since 0.8
 */
public class PostgresFlavor extends GenericFlavor {

    protected PostgresFlavor() {
    }

    public static PostgresFlavor create(DatabaseMetaData metaData) throws SQLException {
        PostgresFlavor flavor = new PostgresFlavor();
        flavor.supportsCatalogs = false;
        flavor.supportsSchemas = true;
        flavor.supportsParamsMetadata = true;
        flavor.supportsBatchUpdates = metaData.supportsBatchUpdates();
        flavor.identifierQuote = metaData.getIdentifierQuoteString();
        return flavor;
    }

    @Override
    public int columnType(int jdbcType, String nativeType) {

        switch (jdbcType) {
            case Types.TIMESTAMP:
                // PostgreSQL driver does not correctly detect Java 8 data types (TIMESTAMP with TZ, etc.)
                // TODO: pull request for Postgres?
                return "timestamptz".equals(nativeType) ? Types.TIMESTAMP_WITH_TIMEZONE : Types.TIMESTAMP;
            // TODO: the same hack for Types.TIME
            default:
                return super.columnType(jdbcType, nativeType);
        }
    }
}
