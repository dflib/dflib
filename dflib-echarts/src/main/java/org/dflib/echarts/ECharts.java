package org.dflib.echarts;

/**
 * A builder of HTML/JS code that renders DataFrame data using ECharts library.
 *
 * @since 1.0.0-M21
 */
public class ECharts {

    /**
     * Starts a builder for a new line chart.
     */
    public static EChart lineChart() {
        return new EChart(SeriesOpts.line());
    }

    /**
     * Starts a builder for a new named line chart.
     */
    public static EChart lineChart(String title) {
        return lineChart().title(title);
    }

    /**
     * Starts a builder for a new bar chart.
     */
    public static EChart barChart() {
        return new EChart(SeriesOpts.bar());
    }

    /**
     * Starts a builder for a new named bar chart.
     */
    public static EChart barChart(String title) {
        return barChart().title(title);
    }

    /**
     * Starts a builder for a new scatter chart.
     */
    public static EChart scatterChart() {
        return new EChart(SeriesOpts.scatter());
    }

    /**
     * Starts a builder for a new named scatter chart.
     */
    public static EChart scatterChart(String title) {
        return scatterChart().title(title);
    }


}
