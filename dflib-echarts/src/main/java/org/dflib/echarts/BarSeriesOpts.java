package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;

public class BarSeriesOpts extends SeriesOpts<BarSeriesOpts> implements CartesianSeriesOpts {

    Integer xAxisIndex;
    Integer yAxisIndex;
    Label label;
    boolean stack;
    Distance barWidth;
    BarItemStyle itemStyle;

    public BarSeriesOpts() {
        // ECharts defaults
        this.stack = false;
    }

    @Override
    public ChartType getType() {
        return ChartType.bar;
    }

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        return CoordinateSystemType.cartesian2d;
    }

    @Override
    public Integer getXAxisIndex() {
        return xAxisIndex;
    }

    @Override
    public Integer getYAxisIndex() {
        return yAxisIndex;
    }

    /**
     * Sets an index of X axis to use for this Series. There can be one or more X axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public BarSeriesOpts xAxisIndex(int index) {
        this.xAxisIndex = index;
        return this;
    }

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public BarSeriesOpts yAxisIndex(int index) {
        this.yAxisIndex = index;
        return this;
    }

    public BarSeriesOpts stack() {
        this.stack = true;
        return this;
    }

    public BarSeriesOpts label(LabelPosition position) {
        this.label = Label.of(position);
        return this;
    }

    public BarSeriesOpts label(Label label) {
        this.label = label;
        return this;
    }

    /**
     * @since 2.0.0
     */
    public BarSeriesOpts barWidthPx(int pixels) {
        this.barWidth = Distance.ofPx(pixels);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public BarSeriesOpts barWidthPct(double percent) {
        this.barWidth = Distance.ofPct(percent);
        return this;
    }

    /**
     * @since 1.1.0
     */
    public BarSeriesOpts itemStyle(BarItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }
}
