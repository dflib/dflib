package org.dflib.echarts;

class OptionXAxisBuilder {
    final String columnName;
    final XAxis axis;

    OptionXAxisBuilder(String columnName, XAxis axis) {
        this.columnName = columnName;
        this.axis = axis;
    }

    XAxis getAxis() {
        return axis;
    }
}
