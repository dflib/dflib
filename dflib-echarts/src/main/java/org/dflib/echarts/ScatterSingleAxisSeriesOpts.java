package org.dflib.echarts;

/**
 * @since 2.0.0
 */
public class ScatterSingleAxisSeriesOpts extends ScatterSeriesOpts<ScatterSingleAxisSeriesOpts> implements SeriesOptsCoordsSingleAxis {

    Integer singleAxisIndex;

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        return CoordinateSystemType.singleAxis;
    }

    @Override
    public Integer getSingleAxisIndex() {
        return singleAxisIndex;
    }

    /**
     * Sets an index of the single axis to use for this Series. There can be one or more axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public ScatterSingleAxisSeriesOpts singleAxisIndex(int index) {
        this.singleAxisIndex = index;
        return this;
    }
}
