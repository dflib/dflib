package com.nhl.dflib.jdbc.connector.metadata.flavors;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class GenericFlavor implements DbFlavor {

    protected boolean supportsCatalogs;
    protected boolean supportsSchemas;
    protected boolean supportsParamsMetadata;
    protected boolean supportsBatchUpdates;
    protected String identifierQuote;

    protected GenericFlavor() {
    }

    public static GenericFlavor create(DatabaseMetaData metaData) throws SQLException {
        GenericFlavor flavor = new GenericFlavor();
        flavor.supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
        flavor.supportsSchemas = metaData.supportsSchemasInTableDefinitions();
        flavor.supportsParamsMetadata = true;
        flavor.supportsBatchUpdates = metaData.supportsBatchUpdates();
        flavor.identifierQuote = metaData.getIdentifierQuoteString();

        return flavor;
    }

    @Override
    public String getIdentifierQuote() {
        return identifierQuote;
    }

    @Override
    public boolean supportsParamsMetadata() {
        return supportsParamsMetadata;
    }

    @Override
    public boolean supportsBatchUpdates() {
        return supportsBatchUpdates;
    }

    @Override
    public boolean supportsCatalogs() {
        return supportsCatalogs;
    }

    @Override
    public boolean supportsSchemas() {
        return supportsSchemas;
    }

    @Override
    public int columnType(int jdbcType, String nativeType) {
        return jdbcType;
    }
}
