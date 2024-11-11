package org.dflib.echarts;

/**
 * Contains rendered JavaScript and HTML parts of a chart.
 */
public class EChartHtml {

    protected final String echartsUrl;
    protected final String chartDiv;
    protected final String chartScript;

    /**
     * @since 2.0.0
     */
    public EChartHtml(String echartsUrl, String chartDiv, String chartScript) {
        this.echartsUrl = echartsUrl;
        this.chartDiv = chartDiv;
        this.chartScript = chartScript;
    }

    /**
     * @since 2.0.0
     */
    public String getEchartsUrl() {
        return echartsUrl;
    }

    /**
     * @since 2.0.0
     */
    public String getChartDiv() {
        return chartDiv;
    }

    /**
     * @since 2.0.0
     */
    public String getChartScript() {
        return chartScript;
    }

    /**
     * @deprecated the property was renamed in the subclass. Keeping here for backwards compatibility.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public String getContainer() {
        return chartDiv;
    }

    /**
     * @deprecated in favor of a bare URL of the ECharts JS library.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public String getExternalScript() {
        String url = echartsUrl != null ? echartsUrl : "";
        return "<script type='text/javascript' src='" + url + "'></script>";
    }

    /**
     * @deprecated in favor of returning bare JavaScript code without script tags.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public String getScript() {
        String script = chartScript != null ? chartScript : "";
        return "<script type='text/javascript'>" + script + "</script>";
    }
}
