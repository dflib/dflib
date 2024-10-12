package org.dflib.parquet.write;

import java.util.List;



public class DataFrameSchema {

    private final List<ColumnMeta> columns;

    public DataFrameSchema(List<ColumnMeta> columns) {
        this.columns = columns;
    }

    public List<ColumnMeta> getColumns() {
        return columns;
    }

}
