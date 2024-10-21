package org.dflib.echarts;

public class ScatterSeriesOpts extends CartesianSeriesOpts<ScatterSeriesOpts> {

    Label label;
    ScatterItemStyle itemStyle;
    Integer symbolSize;

    @Override
    public ChartType getType() {
        return ChartType.scatter;
    }

    public ScatterSeriesOpts label(LabelPosition position) {
        this.label = Label.of(position);
        return this;
    }

    public ScatterSeriesOpts label(Label label) {
        this.label = label;
        return this;
    }

    /**
     * @since 1.1.0
     */
    public ScatterSeriesOpts itemStyle(ScatterItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }

    /**
     * @since 1.1.0
     */
    public ScatterSeriesOpts symbolSize(int symbolSize) {
        this.symbolSize = symbolSize;
        return this;
    }
}
