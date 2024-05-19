package org.dflib.echarts;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.model.ContainerModel;
import org.dflib.echarts.model.ExternalScriptModel;
import org.dflib.echarts.model.ListElementModel;
import org.dflib.echarts.model.ScriptModel;
import org.dflib.echarts.model.SeriesModel;
import org.dflib.echarts.model.XAxisModel;
import org.dflib.series.IntSequenceSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A builder of HTML/JS code that renders DataFrame data using ECharts library.
 *
 * @since 1.0.0-M21
 */
public class ECharts {

    private static final String DEFAULT_ECHARTS_SCRIPT_URL = "https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js";

    private static final Mustache CONTAINER_TEMPLATE = loadTemplate("container.mustache");
    private static final Mustache EXTERNAL_SCRIPT_TEMPLATE = loadTemplate("external_script.mustache");
    private static final Mustache SCRIPT_TEMPLATE = loadTemplate("script.mustache");

    static Mustache loadTemplate(String name) {
        try (InputStream in = ECharts.class.getResourceAsStream(name)) {

            if (in == null) {
                throw new RuntimeException("ECharts 'cell.mustache' template is not found");
            }

            // not providing an explicit resolver of subtemplates.. assuming a single flat template for now
            try (Reader reader = new InputStreamReader(in)) {
                return new DefaultMustacheFactory().compile(reader, name);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading muustache template: " + name, e);
        }
    }

    private final Random rnd;

    private String scriptUrl;

    private String xAxisColumn;
    private Boolean xAxisNoBoundaryGap;

    private String[] dataColumns;
    private EChartType chartType;
    private Map<String, EChartType> chartTypes;

    private String title;
    private String theme;
    private Integer width;
    private Integer height;
    private Boolean legend;

    /**
     * Starts a builder for a new chart.
     */
    public static ECharts chart() {
        return new ECharts();
    }

    /**
     * Starts a builder for a new named chart.
     */
    public static ECharts chart(String title) {
        return chart().title(title);
    }

    protected ECharts() {
        this.rnd = new SecureRandom();
    }

    /**
     * Specifies which DataFrame column should be used to label the X axis. This setting is optional. If not set,
     * series element indices will be used for X.
     */
    public ECharts xAxis(String xAxisColumn) {
        this.xAxisColumn = xAxisColumn;
        return this;
    }

    public ECharts xAxisNoBoundaryGap() {
        this.xAxisNoBoundaryGap = Boolean.TRUE;
        return this;
    }

    /**
     * Specifies which DataFrame columns should be plotted as data "series". A separate plot line (or bars, etc.)
     * will be created for each named column.
     */
    public ECharts data(String... dataColumns) {
        this.dataColumns = dataColumns;
        return this;
    }

    /**
     * Optionally sets the default chart type used by all data series that do not have an explicit type. If the default
     * is not provided here, {@link EChartType#line} is assumed.
     */
    public ECharts chartType(EChartType type) {
        this.chartType = type;
        return this;
    }

    /**
     * Optionally sets the type of the chart for the given named data series. If none is set, and no explicit default is
     * provided, {@link EChartType#line} is assumed.
     */
    public ECharts chartType(String seriesColumn, EChartType type) {
        if (chartTypes == null) {
            chartTypes = new HashMap<>();
        }

        chartTypes.put(seriesColumn, type);
        return this;
    }

    /**
     * Sets an alternative URL of ECharts JavaScript source. If not set, the default public URL is provided.
     */
    public ECharts scriptUrl(String url) {
        this.scriptUrl = url;
        return this;
    }

    public ECharts title(String title) {
        this.title = title;
        return this;
    }

    public ECharts legend() {
        this.legend = Boolean.TRUE;
        return this;
    }

    public ECharts sizePixels(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * @see #darkTheme()
     */
    public ECharts theme(String theme) {
        this.theme = theme;
        return this;
    }

    public ECharts darkTheme() {
        return theme("dark");
    }

    public EChart plot(DataFrame dataFrame) {
        String id = newId();
        return new EChart(
                generateContainerHtml(id),
                generateExternalScriptHtml(),
                generateScriptHtml(id, dataFrame)
        );
    }

    protected String generateContainerHtml(String id) {
        ContainerModel model = new ContainerModel(
                id,
                this.width != null ? this.width : 600,
                this.height != null ? this.height : 400
        );
        return CONTAINER_TEMPLATE.execute(new StringWriter(), model).toString();
    }

    protected String generateExternalScriptHtml() {
        ExternalScriptModel model = new ExternalScriptModel(
                this.scriptUrl != null ? this.scriptUrl : DEFAULT_ECHARTS_SCRIPT_URL
        );
        return EXTERNAL_SCRIPT_TEMPLATE.execute(new StringWriter(), model).toString();
    }

    protected String generateScriptHtml(String id, DataFrame df) {

        ScriptModel model = new ScriptModel(
                id,
                this.title,
                xAxis(df),
                dataSeries(df),
                this.theme,
                this.legend != null ? this.legend : false
        );
        return SCRIPT_TEMPLATE.execute(new StringWriter(), model).toString();
    }

    protected String newId() {
        return "dfl_ech_" + Math.abs(rnd.nextInt(10_000));
    }

    protected XAxisModel xAxis(DataFrame df) {

        // TODO: "getColumn" here would throw on invalid column name, while "cols" in "dataSeries" will
        //  create an empty column... An inconsistency?

        Series<?> xSeries = xAxisColumn != null
                ? df.getColumn(xAxisColumn)
                : new IntSequenceSeries(1, df.height() + 1);

        return new XAxisModel(
                xAxisNoBoundaryGap != null ? xAxisNoBoundaryGap : false,
                xSeries
        );
    }

    protected List<SeriesModel> dataSeries(DataFrame df) {
        String[] seriesColumns = this.dataColumns != null ? this.dataColumns : new String[0];

        EChartType defaultType = this.chartType != null ? this.chartType : EChartType.line;
        Map<String, EChartType> chartTypes = this.chartTypes != null ? this.chartTypes : Map.of();
        String[] types = Arrays.stream(seriesColumns)
                .map(c -> chartTypes.getOrDefault(c, defaultType))
                .map(EChartType::name)
                .toArray(String[]::new);

        DataFrame dataSeries = df.cols(seriesColumns).select();
        return dataSeries.getColumnsIndex().toSeries().toList()
                .stream()
                .map(n -> seriesModel(dataSeries, types, n))
                .collect(Collectors.toList());
    }

    protected SeriesModel seriesModel(DataFrame dataSeries, String[] types, String name) {
        int pos = dataSeries.getColumnsIndex().position(name);
        return new SeriesModel(
                name,
                types[pos],
                seriesData(dataSeries.getColumn(name)),
                pos + 1 == dataSeries.width()
        );
    }

    protected List<ListElementModel> seriesData(Series<?> series) {
        int len = series.size();
        List<ListElementModel> data = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            data.add(new ListElementModel(series.get(i), i + 1 == len));
        }

        return data;
    }
}
