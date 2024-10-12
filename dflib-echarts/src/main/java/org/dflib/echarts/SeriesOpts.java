package org.dflib.echarts;

/**
 * A configuration of a single data series.
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
     * Starts a builder for a scatter series options object.
     */
    public static ScatterSeriesOpts ofScatter() {
        return new ScatterSeriesOpts();
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
}
