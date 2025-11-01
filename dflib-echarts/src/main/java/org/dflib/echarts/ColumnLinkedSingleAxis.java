package org.dflib.echarts;

class ColumnLinkedSingleAxis {
    final String columnName;
    final SingleAxis axis;

    ColumnLinkedSingleAxis(String columnName, SingleAxis axis) {
        this.columnName = columnName;
        this.axis = axis;
    }

    SingleAxis getAxis() {
        return axis;
    }
}
