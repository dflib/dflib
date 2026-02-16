package org.dflib.echarts;

import org.dflib.ByteSource;
import org.dflib.echarts.render.ChartModel;
import org.dflib.echarts.render.ContainerModel;
import org.dflib.echarts.render.ScriptModel;
import org.dflib.echarts.render.util.ElementIdGenerator;
import org.dflib.echarts.render.util.JSMinifier;
import org.dflib.echarts.render.util.Renderer;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Contains rendered JavaScript and HTML parts of a chart.
 */
public class EChartHtml {

    private final String chartDivId;
    private final String echartsUrl;
    private final List<String> themeUrls;
    private final Map<String, ByteSource> maps;

    private final ElementIdGenerator idGenerator;

    // Storing the immutable models of the chart to be able to re-render with other IDs
    private final ContainerModel chartDivModel;
    private final ScriptModel chartScriptModel;
    private final boolean minifyJS;

    /**
     * @since 2.0.0
     */
    public EChartHtml(
            String chartDivId,
            String echartsUrl,
            List<String> themeUrls,
            Map<String, ByteSource> maps,
            ElementIdGenerator idGenerator,
            ContainerModel chartDivModel,
            ScriptModel chartScriptModel,
            boolean minifyJS) {

        this.chartDivId = Objects.requireNonNull(chartDivId);
        this.echartsUrl = echartsUrl;
        this.themeUrls = themeUrls;
        this.maps = maps;
        this.idGenerator = idGenerator;
        this.chartDivModel = chartDivModel;
        this.chartScriptModel = chartScriptModel;
        this.minifyJS = minifyJS;
    }


    /**
     * Set the policy for JS minification when rendering. The default set by {@link EChart} is true.
     *
     * @since 2.0.0
     */
    public EChartHtml minifyJS(boolean minify) {
        return this.minifyJS == minify
                ? this
                : new EChartHtml(
                chartDivId,
                echartsUrl,
                themeUrls,
                maps,
                idGenerator,
                chartDivModel,
                chartScriptModel,
                minify);
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
        return chartDivId.equals(newId) ? this : renderWithId(newId);
    }

    private EChartHtml renderWithId(String newId) {
        return new EChartHtml(
                newId,
                echartsUrl,
                themeUrls,
                maps,
                idGenerator,
                chartDivModel.id(newId),
                chartScriptModel.id(newId),
                minifyJS
        );
    }

    /**
     * Returns a div container ID used by this chart. This ID is already embedded into the chart script and the "div"
     * HTML.
     *
     * @since 2.0.0
     */
    public String getChartDivId() {
        return chartDivId;
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
    public String renderChartDiv() {
        return Renderer.renderContainer(chartDivModel);
    }

    /**
     * @since 2.0.0
     */
    public String renderChartScript() {
        return renderChartScript(false);
    }

    /**
     * @since 2.0.0
     */
    public String renderChartScript(boolean loadECharts) {
        ChartModel model = new ChartModel(chartScriptModel, loadECharts, echartsUrl, themeUrls, resolveMaps(loadECharts));
        String s = Renderer.renderChart(model);
        return minifyJS ? JSMinifier.minify(s) : s;
    }

    /**
     * @deprecated the property was renamed in the subclass. Keeping here for backwards compatibility.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public String getContainer() {
        return Renderer.renderContainer(chartDivModel);
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
        ChartModel model = new ChartModel(chartScriptModel, false, null, null, null);
        String script1 = Renderer.renderChart(model);
        String script = script1 != null ? script1 : "";
        return "<script type='text/javascript'>" + script + "</script>";
    }

    private List<Map.Entry<String, String>> resolveMaps(boolean loadECharts) {
        return !loadECharts || maps == null || maps.isEmpty()
                ? List.of()
                : maps.entrySet().stream().map(e -> Map.entry(e.getKey(), new String(e.getValue().asBytes(), StandardCharsets.UTF_8))).toList();
    }
}
