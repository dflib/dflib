package org.dflib.echarts;

class ColumnLinkedXAxis {
    final String columnName;
    final XAxis axis;

    ColumnLinkedXAxis(String columnName, XAxis axis) {
        this.columnName = columnName;
        this.axis = axis;
    }

    XAxis getAxis() {
        return axis;
    }
}
