package org.dflib.echarts;

/**
 * @since 1.0.0-M22
 */
public class BarSeriesOpts extends CartesianSeriesOpts<BarSeriesOpts> {

    Label label;
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

    public BarSeriesOpts label(LabelPosition position) {
        this.label = Label.of(position);
        return this;
    }

    public BarSeriesOpts label(Label label) {
        this.label = label;
        return this;
    }
}
