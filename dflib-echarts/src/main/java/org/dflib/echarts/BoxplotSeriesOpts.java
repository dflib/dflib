package org.dflib.echarts;

public class BoxplotSeriesOpts extends SeriesOpts<BoxplotSeriesOpts> implements SeriesOptsCoordsCartesian2D {

    Integer xAxisIndex;
    Integer yAxisIndex;
    BoxplotItemStyle itemStyle;

    @Override
    public ChartType getType() {
        return ChartType.boxplot;
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
    public BoxplotSeriesOpts xAxisIndex(int index) {
        this.xAxisIndex = index;
        return this;
    }

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public BoxplotSeriesOpts yAxisIndex(int index) {
        this.yAxisIndex = index;
        return this;
    }

    /**
     * @since 1.1.0
     */
    public BoxplotSeriesOpts itemStyle(BoxplotItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }
}
