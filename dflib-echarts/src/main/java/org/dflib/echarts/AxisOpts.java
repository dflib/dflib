package org.dflib.echarts;

/**
 * @since 1.0.0-M21
 */
public class AxisOpts {

    private AxisType type;
    private AxisLabelOpts axisLabel;
    private boolean boundaryGap;

    public static AxisOpts create() {
        // "true" is the same default as ECharts
        return new AxisOpts(true);
    }

    protected AxisOpts(boolean boundaryGap) {
        this.boundaryGap = boundaryGap;
    }

    public boolean isBoundaryGap() {
        return boundaryGap;
    }

    public AxisOpts boundaryGap(boolean gap) {
        this.boundaryGap = gap;
        return this;
    }

    public AxisLabelOpts getAxisLabel() {
        return axisLabel;
    }

    public AxisOpts axisLabel(AxisLabelOpts axisLabel) {
        this.axisLabel = axisLabel;
        return this;
    }

    public AxisType getType() {
        return type;
    }

    public AxisOpts type(AxisType type) {
        this.type = type;
        return this;
    }
}
