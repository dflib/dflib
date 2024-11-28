package org.dflib.echarts;

/**
 * Enumerates supported chart types.
 */
public enum ChartType {
    line, bar, scatter, pie, candlestick, boxplot;

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
}
