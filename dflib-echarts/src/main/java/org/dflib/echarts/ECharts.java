package org.dflib.echarts;

/**
 * A builder of HTML/JS code that renders DataFrame data using ECharts library.
 *
 * @since 1.0.0-M21
 */
public class ECharts {

    /**
     * Starts a builder for a new chart.
     *
     * @deprecated in favor of {@link EChart#chart()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static EChart chart() {
        return EChart.chart();
    }

    /**
     * Starts a builder for a new named chart.
     *
     * @deprecated in favor of {@link EChart#chart(String)}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static EChart chart(String title) {
        return EChart.chart(title);
    }
}
