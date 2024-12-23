package org.dflib.echarts;

public abstract class CartesianSeriesOpts<SO extends CartesianSeriesOpts<SO>> extends SeriesOpts<SO> {

    protected Integer xAxisIndex;
    protected Integer yAxisIndex;

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        return CoordinateSystemType.cartesian2d;
    }

    /**
     * Sets an index of X axis to use for this Series. There can be one or more X axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public SO xAxisIndex(int index) {
        this.xAxisIndex = index;
        return (SO) this;
    }

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public SO yAxisIndex(int index) {
        this.yAxisIndex = index;
        return (SO) this;
    }
}
