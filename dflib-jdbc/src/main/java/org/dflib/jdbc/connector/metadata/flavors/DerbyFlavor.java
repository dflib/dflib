package org.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public record DerbyFlavor(String identifierQuote, boolean supportsBatchUpdates) implements DbFlavor {

    /**
     * @since 2.0.0
     */
    public static DerbyFlavor of(DatabaseMetaData metaData) throws SQLException {
        return new DerbyFlavor(
                metaData.getIdentifierQuoteString(),
                metaData.supportsBatchUpdates());
    }

    /**
     * @deprecated in favor of {@link #of(DatabaseMetaData)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static DerbyFlavor create(DatabaseMetaData metaData) throws SQLException {
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
        return jdbcType;
    }
}
