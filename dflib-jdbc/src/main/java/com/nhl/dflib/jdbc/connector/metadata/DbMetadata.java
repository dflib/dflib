package com.nhl.dflib.jdbc.connector.metadata;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 0.6
 */
public class DbMetadata {

    private DbFlavor flavor;
    private DatabaseMetaData jdbcMetadata;
    private boolean supportsCatalogs;
    private boolean supportsSchemas;
    private boolean supportsParamsMetadata;
    private boolean supportsBatchUpdates;
    private Map<String, DbTableMetadata> tables;
    private String identifierQuote;

    protected DbMetadata(DbFlavor flavor, DatabaseMetaData jdbcMetadata) {

        this.flavor = Objects.requireNonNull(flavor);
        this.jdbcMetadata = Objects.requireNonNull(jdbcMetadata);

        // TODO: will grow indefinitely, so a potential memory leak... would be great to have a concurrent LRU Map in Java...
        this.tables = new ConcurrentHashMap<>();

        initFlags(flavor, jdbcMetadata);
    }

    public static DbMetadata create(DataSource dataSource) {
        return DbMetadataFactory.create(dataSource);
    }

    protected void initFlags(DbFlavor flavor, DatabaseMetaData jdbcMetadata) {
        switch (flavor) {
            case MYSQL:
            case MARIA_DB:
                // MySQL doesn't support params metadata; but we actually don't know about
                supportsParamsMetadata = false;
                supportsCatalogs = true;
                supportsSchemas = false;
                break;
            case DERBY:
                supportsParamsMetadata = true;
                supportsCatalogs = false;
                supportsSchemas = true;
                break;
            default:
                supportsParamsMetadata = true;
                supportsCatalogs = false;
                supportsSchemas = false;
        }

        try {
            identifierQuote = jdbcMetadata.getIdentifierQuoteString();
            supportsBatchUpdates = jdbcMetadata.supportsBatchUpdates();
        } catch (SQLException e) {
            throw new RuntimeException("Error reading DB metadata", e);
        }
    }

    public DbFlavor getFlavor() {
        return flavor;
    }

    public String getIdentifierQuote() {
        return identifierQuote;
    }

    public boolean supportsParamsMetadata() {
        return supportsParamsMetadata;
    }

    public boolean supportsBatchUpdates() {
        return supportsBatchUpdates;
    }

    public boolean supportsCatalogs() {
        return supportsCatalogs;
    }

    public boolean supportsSchemas() {
        return supportsSchemas;
    }

    public DbTableMetadata getTable(String name) {
        // the name can contain catalogs and schemas... still using the full name as a key, without trying to resolve
        // invariants on the assumption that this is how the caller would like to refer to this table throughout the
        // app... Worst case we'd get some duplicates
        return tables.computeIfAbsent(name, this::loadTableMetadata);
    }

    private DbTableMetadata loadTableMetadata(String tableName) {

        String[] matchParts = toCatalogSchemaName(tableName);
        List<DbColumnMetadata> columns = new ArrayList<>();
        try (ResultSet columnsRs = jdbcMetadata.getColumns(matchParts[0], matchParts[1], matchParts[2], null)) {

            while (columnsRs.next()) {
                String name = columnsRs.getString("COLUMN_NAME");
                int type = columnsRs.getInt("DATA_TYPE");
                columns.add(new DbColumnMetadata(name, type));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting info about table '" + tableName + "'", e);
        }

        // TODO: for now ignoring catalog and schema parts... Using user-provided name
        return new DbTableMetadata(tableName, columns.toArray(new DbColumnMetadata[0]));
    }

    String[] toCatalogSchemaName(String tableName) {

        String[] parts = tableName.split("\\.", 3);
        switch (parts.length) {
            case 1:
                return new String[]{null, null, parts[0]};
            case 2:
                // the first part can be either a catalog or a schema
                if (supportsSchemas) {
                    return new String[]{null, parts[0], parts[1]};
                } else if (supportsCatalogs) {
                    return new String[]{parts[0], null, parts[1]};

                } else {
                    return new String[]{null, null, tableName};
                }
            case 3:
                if (supportsCatalogs && supportsSchemas) {
                    return new String[]{parts[0], parts[1], parts[2]};
                } else {
                    return new String[]{null, null, tableName};
                }

            default:
                return new String[]{null, null, tableName};
        }
    }
}
