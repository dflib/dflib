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
    public static EChartBuilder lineChart() {
        return new EChartBuilder(SeriesOpts.line());
    }

    /**
     * Starts a builder for a new named line chart.
     */
    public static EChartBuilder lineChart(String title) {
        return lineChart().title(title);
    }

    /**
     * Starts a builder for a new bar chart.
     */
    public static EChartBuilder barChart() {
        return new EChartBuilder(SeriesOpts.bar());
    }

    /**
     * Starts a builder for a new named bar chart.
     */
    public static EChartBuilder barChart(String title) {
        return barChart().title(title);
    }

    /**
     * Starts a builder for a new pie chart.
     */
    public static EChartBuilder pieChart() {
        return new EChartBuilder(SeriesOpts.pie());
    }

    /**
     * Starts a builder for a new named pie chart.
     */
    public static EChartBuilder pieChart(String title) {
        return pieChart().title(title);
    }

    /**
     * Starts a builder for a new scatter chart.
     */
    public static EChartBuilder scatterChart() {
        return new EChartBuilder(SeriesOpts.scatter());
    }

    /**
     * Starts a builder for a new named scatter chart.
     */
    public static EChartBuilder scatterChart(String title) {
        return scatterChart().title(title);
    }


}
