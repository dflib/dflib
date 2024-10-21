package org.dflib.echarts;

public class BoxplotSeriesOpts extends CartesianSeriesOpts<BoxplotSeriesOpts> {

    BoxplotItemStyle itemStyle;

    @Override
    public ChartType getType() {
        return ChartType.boxplot;
    }

    /**
     * @since 1.1.0
     */
    public BoxplotSeriesOpts itemStyle(BoxplotItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }
}
