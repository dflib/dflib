package org.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;

public record PostgresFlavor(String identifierQuote, boolean supportsBatchUpdates) implements DbFlavor {

    /**
     * @since 2.0.0
     */
    public static PostgresFlavor of(DatabaseMetaData metaData) throws SQLException {
        return new PostgresFlavor(
                metaData.getIdentifierQuoteString(),
                metaData.supportsBatchUpdates());
    }

    /**
     * @deprecated in favor of {@link #of(DatabaseMetaData)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static PostgresFlavor create(DatabaseMetaData metaData) throws SQLException {
        return of(metaData);
    }

    @Override
    public String getIdentifierQuote() {
        return identifierQuote;
    }

    @Override
    public boolean supportsParamsMetadata() {
        return true;
    }

    @Override
    public boolean supportsCatalogs() {
        return false;
    }

    @Override
    public boolean supportsSchemas() {
        return true;
    }

    @Override
    public int columnType(int jdbcType, String nativeType) {
        return switch (jdbcType) {
            // PostgreSQL driver does not correctly detect Java 8 data types (TIMESTAMP with TZ, etc.)
            // TODO: pull request for Postgres?
            case Types.TIMESTAMP -> "timestamptz".equals(nativeType) ? Types.TIMESTAMP_WITH_TIMEZONE : Types.TIMESTAMP;
            // TODO: the same hack for Types.TIME
            default -> jdbcType;
        };
    }
}
