package org.dflib.echarts;

/**
 * A builder of HTML/JS code that renders DataFrame data using ECharts library.
 *
 * @since 1.0.0-M21
 */
public class ECharts {

    /**
     * Starts a builder for a new chart.
     */
    public static EChartBuilder chart() {
        return new EChartBuilder(series().chartType(EChartType.line).build());
    }

    /**
     * Starts a builder for a new named chart.
     */
    public static EChartBuilder chart(String title) {
        return chart().title(title);
    }

    /**
     * Starts a builder for the default chart series style
     */
    public static EChartSeriesBuilder series() {
        return EChartSeries.series();
    }
}
