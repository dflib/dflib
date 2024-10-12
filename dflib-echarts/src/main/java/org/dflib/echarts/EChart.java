package org.dflib.echarts;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.echarts.render.ContainerModel;
import org.dflib.echarts.render.ExternalScriptModel;
import org.dflib.echarts.render.InitOptsModel;
import org.dflib.echarts.render.ScriptModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

/**
 * A builder of HTML/JS code that renders DataFrame data using ECharts library. Created via {@link ECharts#chart()} and
 * {@link ECharts#chart(String)} methods.
 */
public class EChart {
    private static final String DEFAULT_ECHARTS_SCRIPT_URL = "https://cdn.jsdelivr.net/npm/echarts@5.5.1/dist/echarts.min.js";

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
    private final Option option;
    private String theme;
    private RendererType renderer;
    private String scriptUrl;
    private Integer width;
    private Integer height;

    protected EChart() {
        this.rnd = new SecureRandom();
        this.option = Option.of();
    }

    public EChart renderAsSvg() {
        this.renderer = RendererType.svg;
        return this;
    }

    /**
     * @see #darkTheme()
     */
    public EChart theme(String theme) {
        this.theme = theme;
        return this;
    }

    public EChart darkTheme() {
        return theme("dark");
    }

    /**
     * Sets an alternative URL of ECharts JavaScript source. If not set, the default public URL is provided.
     */
    public EChart scriptUrl(String url) {
        this.scriptUrl = url;
        return this;
    }

    public EChart sizePx(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public EChart toolbox(Toolbox toolbox) {
        option.toolbox(toolbox);
        return this;
    }

    public EChart tooltip(Tooltip tooltip) {
        option.tooltip(tooltip);
        return this;
    }

    /**
     * Adds a Grid to the chart. Grids are used to plot multiple charts in cartesian coordinates. Axis objects can
     * optionally have grid references.
     */
    public EChart grid(Grid grid) {
        option.grid(grid);
        return this;
    }

    /**
     * Adds an X axis to the chart, that will use the specified DataFrame column as axis labels. If this or other
     * "xAxis" methods are not invoked, a default categorical X axis will be generated using element indices for labels
     */
    public EChart xAxis(String dataColumn) {
        option.xAxis(dataColumn);
        return this;
    }

    /**
     * Adds an X axis to the chart, that will use the specified DataFrame column as axis labels. If this or other
     * "xAxis" methods are not invoked, a default categorical X axis will be generated using element indices for labels
     */
    public EChart xAxis(String dataColumn, XAxis axis) {
        option.xAxis(dataColumn, axis);
        return this;
    }

    public EChart xAxis(XAxis axis) {
        option.xAxis(axis);
        return this;
    }

    /**
     * Add one or more configured Y axes of the chart.
     */
    public EChart yAxes(YAxis... axes) {
        option.yAxes(axes);
        return this;
    }

    /**
     * Add a configured Y axis of the chart.
     */
    public EChart yAxis(YAxis axis) {
        option.yAxis(axis);
        return this;
    }

    /**
     * Adds one or more line series to the plot, created from DataFrame columns passed as method arguments.
     */
    public EChart series(String... dataColumns) {
        return series(SeriesOpts.ofLine(), dataColumns);
    }

    // defining individual "series(XyzSeriesOpts, String...)" methods instead of a single "series(SeriesOpts, String...)",
    // so that we can disallow series types not compatible with a single data column (like candlestick).

    /**
     * Adds one or more "line" series to the plot with data coming from DataFrame columns passed as method arguments.
     * Series configuration is specified via the {@link LineSeriesOpts} argument.
     */
    public EChart series(LineSeriesOpts seriesOpts, String... dataColumns) {
        return singleColumnSeries(seriesOpts, dataColumns);
    }

    /**
     * Adds one or more "bar" series to the plot with data coming from DataFrame columns passed as method arguments.
     * Series configuration is specified via the {@link BarSeriesOpts} argument.
     */
    public EChart series(BarSeriesOpts seriesOpts, String... dataColumns) {
        return singleColumnSeries(seriesOpts, dataColumns);
    }

    /**
     * Adds one or more "scatter" series to the plot with data coming from DataFrame columns passed as method arguments.
     * Series configuration is specified via the {@link ScatterSeriesOpts} argument.
     */
    public EChart series(ScatterSeriesOpts seriesOpts, String... dataColumns) {
        return singleColumnSeries(seriesOpts, dataColumns);
    }

    /**
     * Adds one or more "pie" series to the plot with data coming from DataFrame columns passed as method arguments.
     * Series configuration is specified via the {@link PieSeriesOpts} argument.
     */
    public EChart series(PieSeriesOpts seriesOpts, String... dataColumns) {
        return singleColumnSeries(seriesOpts, dataColumns);
    }

    private EChart singleColumnSeries(SeriesOpts<?> seriesOpts, String... dataColumns) {
        for (String c : dataColumns) {
            option.series(seriesOpts, Index.of(c));
        }
        return this;
    }

    /**
     * Adds a "candlestick" series to the chart, with references to 4 data columns indicating "open", "close",
     * "lowest" and "highest" values.
     */
    public EChart series(
            CandlestickSeriesOpts seriesOpts,
            String openColumn,
            String closeColumn,
            String lowestColumn,
            String highestColumn) {

        option.series(seriesOpts, Index.of(
                Objects.requireNonNull(openColumn, "Null 'open' column"),
                Objects.requireNonNull(closeColumn, "Null 'close' column"),
                Objects.requireNonNull(lowestColumn, "Null 'lowest' column"),
                Objects.requireNonNull(highestColumn, "Null 'highest' column")));

        return this;
    }

    /**
     * Adds a "boxplot" series to the chart, with references to 5 data columns indicating "min", "Q1", "median", "Q3",
     * "max" values.
     */
    public EChart series(
            BoxplotSeriesOpts seriesOpts,
            String minColumn,
            String q1Column,
            String medianColumn,
            String q3Column,
            String maxColumn) {

        option.series(seriesOpts, Index.of(
                Objects.requireNonNull(minColumn, "Null 'min' column"),
                Objects.requireNonNull(q1Column, "Null 'q1' column"),
                Objects.requireNonNull(medianColumn, "Null 'median' column"),
                Objects.requireNonNull(q3Column, "Null 'q3' column"),
                Objects.requireNonNull(maxColumn, "Null 'max' column")));

        return this;
    }

    public EChart title(String title) {
        option.title(title);
        return this;
    }

    public EChart legend() {
        option.legend();
        return this;
    }

    public EChartHtml plot(DataFrame dataFrame) {
        String id = newId();
        return new EChartHtml(
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
                this.theme,
                this.renderer != null ? new InitOptsModel(renderer.name()) : null,
                option.resolve(df)
        );
        return SCRIPT_TEMPLATE.execute(new StringWriter(), model).toString();
    }

    protected String newId() {
        return "dfl_ech_" + Math.abs(rnd.nextInt(10_000));
    }
}
