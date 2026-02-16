package org.dflib.echarts.render;

import java.util.List;
import java.util.Map;

/**
 * @since 2.0.0
 */
public record ChartModel(
        ScriptModel chart,
        boolean loadECharts,
        String echartsUrl,
        List<String> themeUrls,
        List<Map.Entry<String, String>> maps) {

    public String getId() {
        return chart.id();
    }
}
