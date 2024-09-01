package org.dflib.echarts;

import org.dflib.echarts.render.option.SeriesModel;

/**
 * A configuration of a single data series.
 *
 * @since 1.0.0-M21
 */
public abstract class SeriesOpts<T extends SeriesOpts<T>> {

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
     * @deprecated in favor of {@link #ofLine()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static LineSeriesOpts line() {
        return ofLine();
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
     * @deprecated in favor of {@link #ofBar()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static BarSeriesOpts bar() {
        return ofBar();
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
     * @deprecated in favor of {@link #ofScatter()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static ScatterSeriesOpts scatter() {
        return ofScatter();
    }

    /**
     * Starts a builder for a pie series options object.
     *
     * @since 1.0.0-M22
     */
    public static PieSeriesOpts ofPie() {
        return new PieSeriesOpts();
    }

    protected SeriesOpts() {
    }


    /**
     * Sets plot series name. Optional, and in most cases the name is taken from the DataFrame column name bound
     * to the series.
     *
     * @since 1.0.0-M23
     */
    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    protected abstract SeriesModel resolve(String name, int labelsPos, int seriesPos, String seriesLayoutBy);
}
