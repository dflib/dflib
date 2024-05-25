package org.dflib.echarts.model;

import java.util.List;

/**
 * A model of a chart dataset row.
 *
 * @since 1.0.0-M21
 */
public class RowModel {

    private final List<ValueModel> columns;
    private final boolean last;

    public RowModel(List<ValueModel> columns, boolean last) {
        this.columns = columns;
        this.last = last;
    }

    public List<ValueModel> getColumns() {
        return columns;
    }

    public boolean isLast() {
        return last;
    }
}
