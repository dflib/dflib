package org.dflib.jdbc.connector.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 0.6
 */
public class DbTableMetadata {

    private TableFQName name;
    private DbColumnMetadata[] columns;

    private Map<String, DbColumnMetadata> columnsByName;
    private DbColumnMetadata[] pk;

    public DbTableMetadata(TableFQName name, DbColumnMetadata[] columns) {
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

    /**
     * @since 0.7
     */
    public TableFQName getName() {
        return name;
    }

    public DbColumnMetadata[] getColumns() {
        return columns;
    }

    public String[] getColumnNames() {
        String[] names = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            names[i] = columns[i].getName();
        }
        return names;
    }

    public DbColumnMetadata[] getPkColumns() {

        if (pk == null) {
            this.pk = findPk();
        }

        return pk;
    }

    /**
     * @since 0.8
     */
    public boolean hasColumn(String name) {
        return columnsByName.containsKey(name);
    }

    public DbColumnMetadata getColumn(String name) {

        DbColumnMetadata column = columnsByName.get(name);
        if (column == null) {
            throw new IllegalArgumentException("Column named '" + name + "' does not exist in table '" + this.name + "'");
        }

        return column;
    }

    private DbColumnMetadata[] findPk() {
        List<DbColumnMetadata> pk = new ArrayList<>();
        for (DbColumnMetadata c : columns) {
            if (c.isPk()) {
                pk.add(c);
            }
        }

        return pk.toArray(new DbColumnMetadata[0]);
    }
}
