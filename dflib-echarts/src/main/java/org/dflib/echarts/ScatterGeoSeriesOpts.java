package org.dflib.echarts;

/**
 * @since 2.0.0
 */
public class ScatterGeoSeriesOpts extends ScatterSeriesOpts<ScatterGeoSeriesOpts> implements
        SeriesOptsCoordsGeo,
        SeriesOptsLonLat {

    String latColumn;
    String lonColumn;
    Integer geoIndex;

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

    @Override
    public Integer getGeoIndex() {
        return geoIndex;
    }

    public ScatterGeoSeriesOpts coordinates(String lonColumn, String latColumn) {
        this.lonColumn = lonColumn;
        this.latColumn = latColumn;
        return this;
    }

    public ScatterGeoSeriesOpts geoIndex(Integer geoIndex) {
        this.geoIndex = geoIndex;
        return this;
    }
}
