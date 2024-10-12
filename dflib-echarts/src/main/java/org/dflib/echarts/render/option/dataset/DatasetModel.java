package org.dflib.echarts.render.option.dataset;

import org.dflib.echarts.render.ValueModels;

/**
 * A model of a chart dataset.
 */
public class DatasetModel {

    private final ValueModels<ValueModels<?>> rows;

    public DatasetModel(ValueModels<ValueModels<?>> rows) {
        this.rows = rows;
    }

    public ValueModels<ValueModels<?>> getRows() {
        return rows;
    }
}
