package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

/**
 * A configuration of a single data series.
 *
 * @since 1.0.0-M21
 */
public abstract class SeriesOpts<T extends SeriesOpts> {

    protected Label label;
    protected Integer yAxisIndex;

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

    protected SeriesOpts() {
    }

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     *
     * @since 1.0.0-M22
     */
    public SeriesOpts yAxisIndex(int index) {
        this.yAxisIndex = index;
        return this;
    }

    /**
     * @since 1.0.0-M22
     */
    public T label(LabelPosition position) {
        this.label = Label.of(position);
        return (T) this;
    }

    /**
     * @since 1.0.0-M22
     */
    public T label(Label label) {
        this.label = label;
        return (T) this;
    }

    protected abstract SeriesModel resolve(String name, EncodeModel encodeModel, String seriesLayoutBy, boolean last);
}
