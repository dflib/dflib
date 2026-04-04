package org.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public record GenericFlavor(
        String identifierQuote,
        boolean supportsParamsMetadata,
        boolean supportsBatchUpdates,
        boolean supportsCatalogs,
        boolean supportsSchemas) implements DbFlavor {

    /**
     * @since 2.0.0
     */
    public static GenericFlavor of(DatabaseMetaData metaData) throws SQLException {
        return new GenericFlavor(
                metaData.getIdentifierQuoteString(),
                true,
                metaData.supportsBatchUpdates(),
                metaData.supportsCatalogsInTableDefinitions(),
                metaData.supportsSchemasInTableDefinitions());
    }

    /**
     * @deprecated in favor of {@link #of(DatabaseMetaData)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static GenericFlavor create(DatabaseMetaData metaData) throws SQLException {
        return of(metaData);
    }

    @Override
    public String getIdentifierQuote() {
        return identifierQuote;
    }

    @Override
    public int columnType(int jdbcType, String nativeType) {
        return jdbcType;
    }
}
