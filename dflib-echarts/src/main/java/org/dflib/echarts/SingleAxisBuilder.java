package org.dflib.echarts;

class SingleAxisBuilder {
    final String columnName;
    final SingleAxis axis;

    SingleAxisBuilder(String columnName, SingleAxis axis) {
        this.columnName = columnName;
        this.axis = axis;
    }

    SingleAxis getAxis() {
        return axis;
    }
}
