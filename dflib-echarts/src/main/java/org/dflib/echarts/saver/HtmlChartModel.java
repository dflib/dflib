package org.dflib.echarts.saver;

/**
 * @since 2.0.0
 */
public class HtmlChartModel {

    private final String chartDiv;
    private final String chartScript;

    public HtmlChartModel(String chartDiv, String chartScript) {
        this.chartDiv = chartDiv;
        this.chartScript = chartScript;
    }

    public String getChartScript() {
        return chartScript;
    }

    public String getChartDiv() {
        return chartDiv;
    }
}
