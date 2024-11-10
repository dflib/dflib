package org.dflib.echarts.saver;

import org.dflib.echarts.EChartHtml;

import java.util.List;

/**
 * @since 2.0.0
 */
public class HtmlPageModel {

    private final String title;
    private final String scriptImport;
    private final List<EChartHtml> charts;

    public HtmlPageModel(String title, String scriptImport, List<EChartHtml> charts) {
        this.title = title;
        this.scriptImport = scriptImport;
        this.charts = charts;
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
