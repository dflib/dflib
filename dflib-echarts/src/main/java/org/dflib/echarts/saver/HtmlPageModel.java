package org.dflib.echarts.saver;

import org.dflib.echarts.EChartHtml;

import java.util.List;

/**
 * @since 2.0.0
 */
public class HtmlPageModel {

    private final String title;
    private final String scriptImport;
    private final String chartStyle;
    private final List<EChartHtml> charts;

    public HtmlPageModel(
            String title,
            String scriptImport,
            String chartStyle,
            List<EChartHtml> charts) {
        this.title = title;
        this.scriptImport = scriptImport;
        this.chartStyle = chartStyle;
        this.charts = charts;
    }

    public String getChartStyle() {
        return chartStyle;
    }

    public String getTitle() {
        return title;
    }

    public String getScriptImport() {
        return scriptImport;
    }

    public List<EChartHtml> getCharts() {
        return charts;
    }
}
