package org.dflib.jdbc.connector.metadata;

import java.util.Objects;

/**
 * DB table fully qualified name that may contain table name, plus optional catalog and schema.
 */
public class TableFQName {

    private String catalog;
    private String schema;
    private String table;

    public TableFQName(String catalog, String schema, String table) {
        this.catalog = catalog != null && catalog.length() == 0 ? null : catalog;
        this.schema = schema != null && schema.length() == 0 ? null : schema;
        this.table = Objects.requireNonNull(table);
    }

    public static TableFQName forName(String tableName) {
        return new TableFQName(null, null, tableName);
    }

    public static TableFQName forSchemaAndName(String schema, String tableName) {
        return new TableFQName(null, schema, tableName);
    }

    public static TableFQName forCatalogAndName(String catalog, String tableName) {
        return new TableFQName(catalog, null, tableName);
    }

    public static TableFQName forCatalogSchemaAndName(String catalog, String schema, String tableName) {
        return new TableFQName(catalog, schema, tableName);
    }

    public boolean hasCatalog() {
        return catalog != null;
    }

    public boolean hasSchema() {
        return schema != null;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (o instanceof TableFQName) {

            TableFQName ot = (TableFQName) o;
            return Objects.equals(catalog, ot.catalog)
                    && Objects.equals(schema, ot.schema)
                    && Objects.equals(table, ot.table);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog, schema, table);
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        if(catalog != null) {
            buffer.append(catalog).append(".");
        }

        if(schema != null) {
            buffer.append(schema).append(".");
        }

        buffer.append(table);
        return buffer.toString();
    }
}
