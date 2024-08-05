package org.dflib.parquet.write;

import java.util.List;

public class DataframeSchema {

    private final List<ColumnMeta> columns;

    public DataframeSchema(List<ColumnMeta> columns) {
        this.columns = columns;
    }

    public List<ColumnMeta> getColumns() {
        return columns;
    }

}
