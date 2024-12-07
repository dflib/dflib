package org.dflib.echarts;

/**
 * @since 2.0.0
 */
public class HeatmapCalendarSeriesOpts extends SeriesOpts<HeatmapCalendarSeriesOpts> {

    Integer calendarIndex;

    @Override
    public ChartType getType() {
        return ChartType.heatmap;
    }

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        return CoordinateSystemType.calendar;
    }

    /**
     * Sets an index of the calendar to use for this Series. The default is 0.
     */
    public HeatmapCalendarSeriesOpts calendarIndex(int index) {
        this.calendarIndex = index;
        return this;
    }
}
