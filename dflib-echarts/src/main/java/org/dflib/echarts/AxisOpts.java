package org.dflib.echarts;

/**
 * @since 1.0.0-M21
 */
public class AxisOpts {

    private AxisLabelOpts axisLabel;
    private Boolean boundaryGap;

    public static AxisOpts create() {
        // "true" is the same default as ECharts
        return new AxisOpts(null).boundaryGap(true);
    }

    protected AxisOpts(Boolean boundaryGap) {
        this.boundaryGap = boundaryGap;
    }

    public boolean isBoundaryGap() {
        return boundaryGap != null ? boundaryGap : false;
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
}
