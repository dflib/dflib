package org.dflib.hardwood.write;

import java.util.List;

/**
 * @since 2.0.0
 */
public class DataFrameSchema {

    private final List<ColumnMeta> columns;

    public DataFrameSchema(List<ColumnMeta> columns) {
        this.columns = columns;
    }

    public List<ColumnMeta> getColumns() {
        return columns;
    }
}
