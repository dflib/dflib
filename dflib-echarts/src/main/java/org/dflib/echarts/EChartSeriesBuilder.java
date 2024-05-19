package org.dflib.echarts;

/**
 * A builder of a single data series object for an {@link EChart}.
 *
 * @since 1.0.0-M21
 */
public class EChartSeriesBuilder {

    private EChartType chartType;
    private Boolean areaStyle;
    private Boolean smooth;
    private Boolean stack;

    protected EChartSeriesBuilder() {
    }

    public EChartSeriesBuilder chartType(EChartType type) {
        this.chartType = type;
        return this;
    }

    public EChartSeriesBuilder areaStyle() {
        this.areaStyle = Boolean.TRUE;
        return this;
    }

    public EChartSeriesBuilder smooth() {
        this.smooth = Boolean.TRUE;
        return this;
    }

    public EChartSeriesBuilder stack() {
        this.stack = Boolean.TRUE;
        return this;
    }

    public EChartSeries build() {
        return new EChartSeries(chartType, areaStyle, smooth, stack);
    }
}
