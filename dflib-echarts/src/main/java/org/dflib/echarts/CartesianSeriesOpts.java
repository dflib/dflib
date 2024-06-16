package org.dflib.echarts;

/**
 * @since 1.0.0-M22
 */
public abstract class CartesianSeriesOpts<T extends CartesianSeriesOpts<T>> extends SeriesOpts {

    protected Label label;
    protected Integer yAxisIndex;

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

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     *
     * @since 1.0.0-M22
     */
    public T yAxisIndex(int index) {
        this.yAxisIndex = index;
        return (T) this;
    }
}
