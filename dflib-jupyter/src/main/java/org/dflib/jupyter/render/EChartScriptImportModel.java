package org.dflib.jupyter.render;

import java.util.Objects;

public class EChartScriptImportModel {

    public final String echartsUrlBase;
    public final String echartsUrlFull;

    public static EChartScriptImportModel of(String echartsUrl) {
        Objects.requireNonNull(echartsUrl, "Null ECharts JS URL");

        return echartsUrl.endsWith(".js")
                ? new EChartScriptImportModel(echartsUrl.substring(0, echartsUrl.length() - 3), echartsUrl)
                : new EChartScriptImportModel(echartsUrl, echartsUrl + ".js");
    }

    public EChartScriptImportModel(
            String echartsUrlBase,
            String echartsUrlFull) {
        this.echartsUrlBase = echartsUrlBase;
        this.echartsUrlFull = echartsUrlFull;
    }
}
