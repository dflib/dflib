package com.nhl.dflib.jdbc.connector.metadata.flavors;

/**
 * Provides DB type specific metadata and strategies for interacting with the database.
 *
 * @since 0.8
 */
public interface DbFlavor {

    String getIdentifierQuote();

    boolean supportsParamsMetadata();

    boolean supportsBatchUpdates();

    boolean supportsCatalogs();

    boolean supportsSchemas();

    int columnType(int jdbcType, String nativeType);
}
