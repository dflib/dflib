package org.dflib.echarts;

/**
 * @since 2.0.0
 */
public class MapSeriesOpts extends SeriesOpts<MapSeriesOpts> implements
        SeriesOptsNamedItems,
        SeriesOptsCoordsGeo {

    String itemNameColumn;
    Integer geoIndex;

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        return CoordinateSystemType.geo;
    }

    @Override
    public Integer getGeoIndex() {
        return geoIndex;
    }

    @Override
    public ChartType getType() {
        return ChartType.map;
    }

    @Override
    public String getItemNameSeries() {
        return itemNameColumn;
    }

    public MapSeriesOpts itemNameData(String itemNameColumn) {
        this.itemNameColumn = itemNameColumn;
        return this;
    }

    public MapSeriesOpts geoIndex(Integer geoIndex) {
        this.geoIndex = geoIndex;
        return this;
    }
}
