package org.dflib.echarts;

/**
 * An entry point to EChart builder. The builder would create HTML/JS code that renders DataFrame data using ECharts
 * library.
 */
public class ECharts {

    /**
     * Starts a builder for a new chart.
     */
    public static EChart chart() {
        return new EChart();
    }

    /**
     * Starts a builder for a new named chart.
     */
    public static EChart chart(String title) {
        return chart().title(title);
    }
}
