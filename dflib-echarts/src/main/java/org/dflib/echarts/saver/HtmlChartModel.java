package org.dflib.echarts.saver;

import java.util.function.Supplier;

/**
 * @since 2.0.0
 */
// using suppliers for lazy rendering, as not all properties may be present in the template
public record HtmlChartModel(
        String id,
        Supplier<String> chartDivMaker,
        Supplier<String> chartScriptMaker,
        Supplier<String> echartsLoadScriptMaker) {

    public String chartDiv() {
        return chartDivMaker.get();
    }

    public String chartScript() {
        return chartScriptMaker.get();
    }

    public String echartsLoadScript() {
        return echartsLoadScriptMaker.get();
    }
}
