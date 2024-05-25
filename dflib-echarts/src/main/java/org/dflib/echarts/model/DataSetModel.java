package org.dflib.echarts.model;

import java.util.List;

/**
 * A model of a chart dataset.
 *
 * @since 1.0.0-M21
 */
public class DataSetModel {

    private final List<RowModel> rows;

    public DataSetModel(List<RowModel> rows) {
        this.rows = rows;
    }

    public List<RowModel> getRows() {
        return rows;
    }
}
