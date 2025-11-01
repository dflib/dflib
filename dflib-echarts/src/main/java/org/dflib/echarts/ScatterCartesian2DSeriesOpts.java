package org.dflib.echarts;

/**
 * @since 2.0.0
 */
public class ScatterCartesian2DSeriesOpts extends ScatterSeriesOpts<ScatterCartesian2DSeriesOpts> implements SeriesOptsCoordsCartesian2D {

    Integer xAxisIndex;
    Integer yAxisIndex;

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
    public ScatterCartesian2DSeriesOpts xAxisIndex(int index) {
        this.xAxisIndex = index;
        return this;
    }

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public ScatterCartesian2DSeriesOpts yAxisIndex(int index) {
        this.yAxisIndex = index;
        return this;
    }
}
