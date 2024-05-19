package org.dflib.echarts;

/**
 * Configuration of a single data series of an {@link EChart}.
 *
 * @since 1.0.0-M21
 */
public class EChartSeries {

    private final EChartType chartType;

    // using objects instead of primitives to be able to perform merge
    private final Boolean areaStyle;
    private final Boolean smooth;
    private final Boolean stack;

    /**
     * Starts a builder for the default chart series style
     */
    public static EChartSeriesBuilder series() {
        return new EChartSeriesBuilder();
    }

    protected EChartSeries(
            EChartType chartType,
            Boolean areaStyle,
            Boolean smooth,
            Boolean stack) {

        this.chartType = chartType;
        this.areaStyle = areaStyle;
        this.smooth = smooth;
        this.stack = stack;
    }

    public EChartSeries merge(EChartSeries other) {
        return new EChartSeries(
                other.chartType != null ? other.chartType : this.chartType,
                other.areaStyle != null ? other.areaStyle : this.areaStyle,
                other.smooth != null ? other.smooth : this.smooth,
                other.stack != null ? other.stack : this.stack
        );
    }

    public boolean isAreaStyle() {
        return areaStyle != null ? areaStyle : false;
    }

    public boolean isSmooth() {
        return smooth != null ? smooth : false;
    }

    public boolean isStack() {
        return stack != null ? stack : false;
    }

    public EChartType getChartType() {
        return chartType != null ? chartType : EChartType.line;
    }
}
