package org.dflib.echarts;

import org.dflib.echarts.render.ContainerModel;
import org.dflib.echarts.render.ScriptModel;
import org.dflib.echarts.render.util.ElementIdGenerator;
import org.dflib.echarts.render.util.Renderer;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Contains rendered JavaScript and HTML parts of a chart.
 */
public class EChartHtml {

    private final String divId;
    private final String echartsUrl;
    private final List<String> themeUrls;
    private final String chartDiv;
    private final String chartScript;
    private final Function<String, String> echartsLoadScriptMaker;

    // Storing the immutable models of the chart here to be able to re-render with other IDs
    private final ElementIdGenerator idGenerator;
    private final ContainerModel containerModel;
    private final ScriptModel scriptModel;

    /**
     * @since 2.0.0
     */
    public EChartHtml(
            String divId,
            String echartsUrl,
            List<String> themeUrls,
            String chartDiv,
            String chartScript,
            Function<String, String> echartsLoadScriptMaker,
            ElementIdGenerator idGenerator,
            ContainerModel containerModel,
            ScriptModel scriptModel) {

        this.divId = Objects.requireNonNull(divId);
        this.echartsUrl = echartsUrl;
        this.themeUrls = themeUrls;
        this.chartDiv = chartDiv;
        this.chartScript = chartScript;
        this.echartsLoadScriptMaker = echartsLoadScriptMaker;

        this.idGenerator = idGenerator;
        this.containerModel = containerModel;
        this.scriptModel = scriptModel;
    }

    /**
     * Returns a new copy of this chart re-rendered with a new unique div container ID for the chart div.
     *
     * @since 2.0.0
     */
    public EChartHtml plotWithNewDivId() {
        return plotWithDivId(idGenerator.nextId());
    }

    /**
     * Returns a new copy of this chart re-rendered with a new div container ID for the chart div.
     *
     * @since 2.0.0
     */
    public EChartHtml plotWithDivId(String newId) {
        return divId.equals(newId) ? this : renderWithId(newId);
    }

    private EChartHtml renderWithId(String newId) {

        ContainerModel containerModelWithId = containerModel.id(newId);
        ScriptModel scriptModelWithId = scriptModel.id(newId);

        return new EChartHtml(
                newId,
                echartsUrl,
                themeUrls,
                Renderer.renderContainer(containerModelWithId),
                Renderer.renderScript(scriptModelWithId),
                echartsLoadScriptMaker,
                idGenerator,
                containerModelWithId,
                scriptModelWithId
        );
    }

    /**
     * Returns a div container ID used by this chart. This ID is already embedded into the chart script and the "div"
     * HTML.
     *
     * @since 2.0.0
     */
    public String getDivId() {
        return divId;
    }

    /**
     * @since 2.0.0
     */
    public String getEchartsUrl() {
        return echartsUrl;
    }

    /**
     * Returns a List of JavaScript URLs of optional extra themes required to render the chart. The two standard themes
     * (light and dark) are bundled in the main ECharts script and will not be present in this list.
     *
     * @since 2.0.0
     */
    public List<String> getThemeUrls() {
        return themeUrls;
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
     * @since 2.0.0
     */
    public Function<String, String> getEchartsLoadScriptMaker() {
        return echartsLoadScriptMaker;
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
