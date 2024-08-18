package org.dflib.echarts.render.option.dataset;

import java.util.List;

/**
 * A model of a chart dataset.
 *
 * @since 1.0.0-M21
 */
public class DataSetModel {

    private final List<DataSetRowModel> rows;

    public DataSetModel(List<DataSetRowModel> rows) {
        this.rows = rows;
    }

    public List<DataSetRowModel> getRows() {
        return rows;
    }
}
