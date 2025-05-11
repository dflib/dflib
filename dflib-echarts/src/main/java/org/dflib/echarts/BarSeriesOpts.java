package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;

public class BarSeriesOpts extends CartesianSeriesOpts<BarSeriesOpts> {

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
