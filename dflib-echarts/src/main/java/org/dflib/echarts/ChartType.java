package org.dflib.echarts;

/**
 * Enumerates supported chart types.
 */
public enum ChartType {
    line, bar, scatter, pie, candlestick, boxplot;

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
