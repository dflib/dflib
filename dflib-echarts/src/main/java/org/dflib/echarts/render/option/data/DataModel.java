package org.dflib.echarts.render.option.data;

import org.dflib.echarts.render.ValueModels;

/**
 * @since 2.0.0
 */
public class DataModel {

    private final ValueModels<ValueModels<?>> rows;

    public DataModel(ValueModels<ValueModels<?>> rows) {
        this.rows = rows;
    }

    public ValueModels<ValueModels<?>> getRows() {
        return rows;
    }
}
