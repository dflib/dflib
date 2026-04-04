package org.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public record MySQLFlavor(String identifierQuote, boolean supportsBatchUpdates) implements DbFlavor {

    /**
     * @since 2.0.0
     */
    public static MySQLFlavor of(DatabaseMetaData metaData) throws SQLException {
        return new MySQLFlavor(
                metaData.getIdentifierQuoteString(),
                metaData.supportsBatchUpdates());
    }

    /**
     * @deprecated in favor of {@link #of(DatabaseMetaData)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static MySQLFlavor create(DatabaseMetaData metaData) throws SQLException {
        return of(metaData);
    }

    @Override
    public String getIdentifierQuote() {
        return identifierQuote;
    }

    @Override
    public boolean supportsParamsMetadata() {
        // with "generateSimpleParameterMetadata=true" we are getting back parameters, but they are all VARCHAR,
        // so rather useless
        return false;
    }

    @Override
    public boolean supportsCatalogs() {
        return true;
    }

    @Override
    public boolean supportsSchemas() {
        return false;
    }

    @Override
    public int columnType(int jdbcType, String nativeType) {
        return jdbcType;
    }
}
