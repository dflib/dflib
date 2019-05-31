package com.nhl.dflib.jdbc.connector.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.6
 */
public class DbTableMetadata {

    private String name;
    private DbColumnMetadata[] columns;
    private Map<String, DbColumnMetadata> columnsByName;

    public DbTableMetadata(String name, DbColumnMetadata[] columns) {
        this.name = name;
        this.columns = columns;
        this.columnsByName = new HashMap<>();

        for (DbColumnMetadata column : columns) {

            DbColumnMetadata existing = columnsByName.put(column.getName(), column);
            if (existing != null && existing != column) {
                throw new IllegalArgumentException("Duplicate column name: " + column.getName());
            }
        }
    }

    public String getName() {
        return name;
    }

    public DbColumnMetadata[] getColumns() {
        return columns;
    }

    public DbColumnMetadata[] getPkColumns() {
        throw new UnsupportedOperationException("TODO");
    }

    public DbColumnMetadata getColumn(String name) {

        DbColumnMetadata column = columnsByName.get(name);
        if (column == null) {
            throw new IllegalArgumentException("Column named '" + name + "' does not exist in table '" + this.name + "'");
        }

        return column;
    }
}
