package org.dflib.echarts;

class XAxisBuilder {
    final String columnName;
    final XAxis axis;

    XAxisBuilder(String columnName, XAxis axis) {
        this.columnName = columnName;
        this.axis = axis;
    }

    XAxis getAxis() {
        return axis;
    }
}
