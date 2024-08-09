package org.dflib.parquet.write;

import java.util.List;

/**
 * @since 1.0.0-M23
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
