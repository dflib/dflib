package org.dflib.echarts;

/**
 * A configuration of a single data series.
 *
 * @since 1.0.0-M21
 */
public abstract class SeriesOpts<SO extends SeriesOpts<SO>> {

    protected String name;

    /**
     * Starts a builder for a line series options object.
     *
     * @since 1.0.0-M22
     */
    public static LineSeriesOpts ofLine() {
        return new LineSeriesOpts();
    }

    /**
     * Starts a builder for a bar series options object.
     *
     * @since 1.0.0-M22
     */
    public static BarSeriesOpts ofBar() {
        return new BarSeriesOpts();
    }

    /**
     * Starts a builder for a scatter series options object.
     *
     * @since 1.0.0-M22
     */
    public static ScatterSeriesOpts ofScatter() {
        return new ScatterSeriesOpts();
    }

    /**
     * Starts a builder for a pie series options object.
     *
     * @since 1.0.0-M22
     */
    public static PieSeriesOpts ofPie() {
        return new PieSeriesOpts();
    }

    /**
     * Starts a builder for a candlestick series options object.
     *
     * @since 1.0.0-RC1
     */
    public static CandlestickSeriesOpts ofCandlestick() {
        return new CandlestickSeriesOpts();
    }

    protected SeriesOpts() {
    }

    /**
     * Sets plot series name. Optional, and in most cases the name is taken from the DataFrame column name bound
     * to the series.
     *
     * @since 1.0.0-M23
     */
    public SO name(String name) {
        this.name = name;
        return (SO) this;
    }

    /**
     * @since 1.0.0-M23
     */
    public abstract ChartType getType();
}
