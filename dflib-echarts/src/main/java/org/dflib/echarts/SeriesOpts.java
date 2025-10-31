package org.dflib.echarts;

/**
 * A single visual data series. It has a number of subclasses, each corresponding to a chart type. A full list of
 * supported types is defined in {@link ChartType} enum.
 */
public abstract class SeriesOpts<SO extends SeriesOpts<SO>> {

    protected String name;

    /**
     * Starts a builder for a line series options object.
     */
    public static LineSeriesOpts ofLine() {
        return new LineSeriesOpts();
    }

    /**
     * Starts a builder for a bar series options object.
     */
    public static BarSeriesOpts ofBar() {
        return new BarSeriesOpts();
    }

    /**
     * Starts a builder for a scatter series options object with cartesian coordinate system.
     */
    public static ScatterCartesian2DSeriesOpts ofScatter() {
        return new ScatterCartesian2DSeriesOpts();
    }

    /**
     * Starts a builder for a scatter series options object with single axis coordinate system.
     *
     * @since 2.0.0
     */
    public static ScatterSingleAxisSeriesOpts ofScatterSingleAxis() {
        return new ScatterSingleAxisSeriesOpts();
    }

    /**
     * Starts a builder for a pie series options object.
     */
    public static PieSeriesOpts ofPie() {
        return new PieSeriesOpts();
    }

    /**
     * Starts a builder for a candlestick series options object.
     */
    public static CandlestickSeriesOpts ofCandlestick() {
        return new CandlestickSeriesOpts();
    }


    /**
     * Starts a builder for a boxplot series options object.
     */
    public static BoxplotSeriesOpts ofBoxplot() {
        return new BoxplotSeriesOpts();
    }

    /**
     * Starts a builder for a heatmap series options object rendered in cartesian coordinates.
     *
     * @since 2.0.0
     */
    public static HeatmapCartesian2DSeriesOpts ofHeatmap() {
        return new HeatmapCartesian2DSeriesOpts();
    }

    /**
     * Starts a builder for a heatmap series options object rendered in calendar coordinates.
     *
     * @since 2.0.0
     */
    public static HeatmapCalendarSeriesOpts ofHeatmapCalendar() {
        return new HeatmapCalendarSeriesOpts();
    }

    protected SeriesOpts() {
    }

    /**
     * Sets plot series name. Optional, and in most cases the name is taken from the DataFrame column name bound
     * to the series.
     */
    public SO name(String name) {
        this.name = name;
        return (SO) this;
    }

    public abstract ChartType getType();

    /**
     * Returns the type of coordinate system used by the series.
     *
     * @since 2.0.0
     */
    public abstract CoordinateSystemType getCoordinateSystemType();
}
