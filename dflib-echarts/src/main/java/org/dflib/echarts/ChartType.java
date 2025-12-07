package org.dflib.echarts;

/**
 * Enumerates supported chart types.
 */
public enum ChartType {
    line, bar, scatter, pie, candlestick, boxplot,

    /**
     * @since 2.0.0
     */
    heatmap;

    /**
     * @deprecated in favor of {@link CoordinateSystemType#isCartesian()}, as many chart types can be plotted over
     * more than one coordinate system.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public boolean isCartesian() {
        switch (this) {
            case bar:
            case boxplot:
            case candlestick:
            case line:
            case scatter:
                return true;
            case pie:
                return false;
            default:
                throw new UnsupportedOperationException("Unexpected ChartType: " + this);
        }
    }

    /**
     * Returns whether the data for this chart type should be rendered as a dataset or inline for each series.
     *
     * @since 2.0.0
     */
    public boolean supportsDataset() {
        return switch (this) {
            // TODO: dataset / encode kinda works with heatmaps, but is very unreliable, so should encode data
            //  per Series for those
            case heatmap -> false;
            default -> true;
        };
    }
}
