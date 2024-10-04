package org.dflib.echarts;

/**
 * Enumerates supported chart types.
 *
 * @since 1.0.0-M21
 */
public enum ChartType {
    line, bar, scatter, pie, candlestick, boxplot;

    /**
     * @since 1.0.0-M23
     */
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
