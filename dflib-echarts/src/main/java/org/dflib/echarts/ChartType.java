package org.dflib.echarts;

/**
 * Enumerates supported chart types.
 *
 * @since 1.0.0-M21
 */
public enum ChartType {
    line, bar, scatter, pie;

    /**
     * @since 1.0.0-M23
     */
    public boolean isCartesian() {
        switch (this) {
            case line:
            case bar:
            case scatter:
                return true;
            case pie:
                return false;
            default:
                throw new UnsupportedOperationException("Unexpected ChartType: " + this);
        }
    }
}
