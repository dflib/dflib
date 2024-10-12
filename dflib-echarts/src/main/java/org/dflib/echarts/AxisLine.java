package org.dflib.echarts;

import org.dflib.echarts.render.option.axis.AxisLineModel;

public class AxisLine {

    private boolean show;
    private Boolean onZero;

    public static AxisLine of() {
        return new AxisLine();
    }

    protected AxisLine() {
        // "true" is default in ECharts, but still needs to be shown explicitly per this documentation comment:
        // "The value axis doesn't show the axis line by default since v5.0.0, you need to explicitly set axisLine.show
        // as true to enable it."
        this.show = true;
    }

    public AxisLine show(boolean show) {
        this.show = show;
        return this;
    }

    public AxisLine onZero(boolean onZero) {
        this.onZero = onZero;
        return this;
    }

    protected AxisLineModel resolve() {
        return new AxisLineModel(show, onZero);
    }
}
