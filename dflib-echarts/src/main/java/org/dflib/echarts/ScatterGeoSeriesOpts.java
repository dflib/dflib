package org.dflib.echarts;

/**
 * @since 2.0.0
 */
public class ScatterGeoSeriesOpts extends ScatterSeriesOpts<ScatterGeoSeriesOpts> implements SeriesOptsGeoCoordinates {

    String latColumn;
    String lonColumn;

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        return CoordinateSystemType.geo;
    }

    @Override
    public String getLonSeries() {
        return lonColumn;
    }

    @Override
    public String getLatSeries() {
        return latColumn;
    }

    public ScatterGeoSeriesOpts coordinates(String lonColumn, String latColumn) {
        this.lonColumn = lonColumn;
        this.latColumn = latColumn;
        return this;
    }
}
