package org.dflib.echarts;

/**
 * @since 1.0.0-M22
 */
public class BarSeriesOpts extends CartesianSeriesOpts<BarSeriesOpts> {

    boolean stack;

    public BarSeriesOpts() {
        // ECharts defaults
        this.stack = false;
    }

    @Override
    public ChartType getType() {
        return ChartType.bar;
    }

    public BarSeriesOpts stack() {
        this.stack = true;
        return this;
    }
}
