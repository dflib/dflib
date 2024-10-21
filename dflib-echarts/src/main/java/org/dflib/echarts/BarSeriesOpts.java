package org.dflib.echarts;

public class BarSeriesOpts extends CartesianSeriesOpts<BarSeriesOpts> {

    Label label;
    boolean stack;
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
     * @since 1.1.0
     */
    public BarSeriesOpts itemStyle(BarItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }
}
