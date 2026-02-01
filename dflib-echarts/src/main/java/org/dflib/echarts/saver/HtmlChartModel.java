package org.dflib.echarts.saver;

import java.util.function.Function;

/**
 * @since 2.0.0
 */
public record HtmlChartModel(
        String id,
        String chartDiv,
        String chartScript,
        Function<String, String> echartsLoadScriptMaker) {

    public String echartsLoadScript() {
        return echartsLoadScriptMaker.apply(id);
    }
}
