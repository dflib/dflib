package com.nhl.dflib.jdbc.connector.metadata;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 0.6
 */
public class DbMetadata {

    private DataSource dataSource;
    private DbFlavor flavor;
    private boolean supportsCatalogs;
    private boolean supportsSchemas;
    private boolean supportsParamsMetadata;
    private boolean supportsBatchUpdates;
    private String identifierQuote;
    private Map<TableFQName, DbTableMetadata> tables;

    protected DbMetadata(DataSource dataSource, DbFlavor flavor, DatabaseMetaData jdbcMetadata) {

        // note that we can't cache DatabaseMetaData, as it stops working once the underlying connection is closed,
        // so keeping the DataSource around to get it back whenever we need to compile table info, etc.

        this.dataSource = dataSource;
        this.flavor = Objects.requireNonNull(flavor);

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
        TableFQName fqName = parseTableName(name);
        return tables.computeIfAbsent(fqName, this::loadTableMetadata);
    }

    /**
     * @since 0.7
     */
    public DbTableMetadata getTable(TableFQName tableName) {

        // the name can be full or partial (i.e. catalogs and schemas may or may not be there). Not trying to resolve
        // invariants on the assumption that this is how the caller would like to refer to this table throughout the
        // app... Worst case we'd get some duplicates

        return tables.computeIfAbsent(tableName, this::loadTableMetadata);
    }

    /**
     * Parses a table name String into a fully-qualified name object that provides information about table schema,
     * catalog and name. The incoming
     *
     * @param tableName a String identifying either fully-qualified or partial table name
     * @return a {@link TableFQName} object corresponding to the table.
     * @since 0.7
     */
    // TODO: currently two related issues with this method
    //   1. will not work for DBs like Hana that are using dots as separators of their internal namespace that is
    //   otherwise the part of the table name.
    //   2. If table name components are passed in already quoted, this parser will not strip the quotes and hence
    //   will produce an incorrect FQN. (e.g. "c"."n")
    public TableFQName parseTableName(String tableName) {


        String[] parts = tableName.split("\\.", 3);
        switch (parts.length) {
            case 1:
                return new TableFQName(null, null, parts[0]);
            case 2:
                // the first part can be either a catalog or a schema
                if (supportsSchemas) {
                    return new TableFQName(null, parts[0], parts[1]);
                } else if (supportsCatalogs) {
                    return new TableFQName(parts[0], null, parts[1]);

                } else {
                    return new TableFQName(null, null, tableName);
                }
            case 3:
                if (supportsCatalogs && supportsSchemas) {
                    return new TableFQName(parts[0], parts[1], parts[2]);
                } else {
                    return new TableFQName(null, null, tableName);
                }

            default:
                return new TableFQName(null, null, tableName);
        }
    }

    private DbTableMetadata loadTableMetadata(TableFQName tableName) {

        Map<String, Integer> columnsAndTypes = new LinkedHashMap<>();
        Set<String> pks = new HashSet<>();
        Set<String> nullable = new HashSet<>();

        try (Connection c = dataSource.getConnection()) {

            DatabaseMetaData md = c.getMetaData();

            try (ResultSet columnsRs = md.getColumns(
                    tableName.getCatalog(), tableName.getSchema(), tableName.getTable(), null)) {

                while (columnsRs.next()) {
                    String name = columnsRs.getString("COLUMN_NAME");
                    int type = columnsRs.getInt("DATA_TYPE");
                    columnsAndTypes.put(name, type);

                    if ("YES".equals(columnsRs.getString("IS_NULLABLE"))) {
                        nullable.add(name);
                    }
                }
            }

            try (ResultSet pkRs = md.getPrimaryKeys(
                    tableName.getCatalog(), tableName.getSchema(), tableName.getTable())) {

                while (pkRs.next()) {
                    pks.add(pkRs.getString("COLUMN_NAME"));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting info about table '" + tableName + "'", e);
        }


        int len = columnsAndTypes.size();
        DbColumnMetadata[] columns = new DbColumnMetadata[len];
        int i = 0;
        for (Map.Entry<String, Integer> e : columnsAndTypes.entrySet()) {
            String name = e.getKey();
            columns[i++] = new DbColumnMetadata(
                    name,
                    e.getValue(),
                    pks.contains(name),
                    nullable.contains(name));
        }

        return new DbTableMetadata(tableName, columns);
    }
}
