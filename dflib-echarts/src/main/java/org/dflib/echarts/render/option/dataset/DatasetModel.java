package org.dflib.echarts.render.option.dataset;

import java.util.List;

/**
 * A model of a chart dataset.
 *
 * @since 1.0.0-M21
 */
public class DatasetModel {

    private final List<DatasetRowModel> rows;

    public DatasetModel(List<DatasetRowModel> rows) {
        this.rows = rows;
    }

    public List<DatasetRowModel> getRows() {
        return rows;
    }
}
