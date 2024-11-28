package org.dflib.echarts;

/**
 * @since 2.0.0
 */
// heatmap is not always cartesian and can have different coordinate systems.
// hence, not inheriting from CartesianSeriesOpts
public class HeatmapSeriesOpts extends SeriesOpts<HeatmapSeriesOpts> {

    CoordinateSystem coordinateSystem;

    Integer xAxisIndex;
    Integer yAxisIndex;
    Integer calendarIndex;

    @Override
    public ChartType getType() {
        return ChartType.heatmap;
    }

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        return coordinateSystem != null
                ? coordinateSystem.getCoordinateSystemType()
                : CoordinateSystemType.cartesian2d;
    }

    /**
     * Configures the series to be plotted on the 2D cartesian coordinate system. This is the default and doesn't
     * need to be set explicitly.
     */
    public HeatmapSeriesOpts cartesian2D() {
        this.coordinateSystem = CoordinateSystem.cartesian2d;
        return this;
    }

    /**
     * Configures the series to be plotted on calendar coordinate system
     */
    public HeatmapSeriesOpts calendar() {
        this.coordinateSystem = CoordinateSystem.calendar;
        return this;
    }

    /**
     * Sets an index of X axis to use for this Series. Only applicable for {@link #cartesian2D()} type of heatmaps.
     * The default is 0.
     */
    public HeatmapSeriesOpts xAxisIndex(int index) {
        this.xAxisIndex = index;
        return this;
    }

    /**
     * Sets an index of Y axis to use for this Series. Only applicable for {@link #cartesian2D()} type of heatmaps.
     * The default is 0.
     */
    public HeatmapSeriesOpts yAxisIndex(int index) {
        this.yAxisIndex = index;
        return this;
    }

    /**
     * Sets an index of the calendar to use for this Series. Only applicable for {@link #calendar()} type of heatmaps.
     * The default is 0.
     */
    public HeatmapSeriesOpts calendarIndex(int index) {
        this.calendarIndex = index;
        return this;
    }

    enum CoordinateSystem {
        cartesian2d, calendar; // TODO: "geo"

        CoordinateSystemType getCoordinateSystemType() {

            switch (this) {
                case cartesian2d:
                    return CoordinateSystemType.cartesian2d;
                case calendar:
                    return CoordinateSystemType.calendar;
                default:
                    throw new IllegalStateException("Can't map this coordinate system to a type:" + name());
            }
        }
    }
}
