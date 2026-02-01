package org.dflib.echarts.render;

import java.util.List;

/**
 * @since 2.0.0
 */
public record ChartModel(ScriptModel chart, boolean loadECharts, String echartsUrl, List<String> themeUrls) {

    public String getId() {
        return chart.id();
    }
}
