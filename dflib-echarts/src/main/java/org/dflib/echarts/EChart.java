package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.echarts.render.ContainerModel;
import org.dflib.echarts.render.InitOptsModel;
import org.dflib.echarts.render.ScriptModel;
import org.dflib.echarts.render.util.ElementIdGenerator;
import org.dflib.echarts.render.util.Renderer;

import java.util.Objects;

/**
 * A builder of HTML/JS code that renders DataFrame data using ECharts library. Created via {@link ECharts#chart()} and
 * {@link ECharts#chart(String)} methods.
 */
public class EChart {

    private static final String DEFAULT_ECHARTS_SCRIPT_URL = "https://cdn.jsdelivr.net/npm/echarts@5.6.0/dist/echarts.min.js";

    private final ElementIdGenerator idGenerator;
    private final Option option;
    private String theme;
    private RendererType renderer;
    private String scriptUrl;
    private Integer width;
    private Integer height;

    @Deprecated(since = "2.0.0", forRemoval = true)
    protected EChart() {
        this(ElementIdGenerator.random());
    }

    protected EChart(ElementIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
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
     * @since 2.0.0
     */
    public EChart visualMap(VisualMap visualMap) {
        option.visualMap(visualMap);
        return this;
    }

    /**
     * Adds a calendar coordinate system to the chart, that will use the specified DataFrame column to plot dates.
     * Since we don't specify a date range for the calendar in this method, the period of the last 12 months back
     * from the current date is assumed. Use {@link #calendar(String, CalendarCoords)} to set the exact range.
     *
     * @since 2.0.0
     */
    public EChart calendar(String dataColumn) {
        option.calendar(dataColumn);
        return this;
    }

    /**
     * Adds a calendar coordinate system to the chart, that will use the specified DataFrame column to plot dates.
     *
     * @since 2.0.0
     */
    public EChart calendar(String dataColumn, CalendarCoords calendar) {
        option.calendar(dataColumn, calendar);
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

    /**
     * Adds a "heatmap" series to the plot with data coming from the DataFrame column passed as method arguments.
     * The plot will be done on a calendar coordinate system.  Series configuration is specified via the
     * {@link HeatmapCalendarSeriesOpts} argument.
     *
     * @since 2.0.0
     */
    public EChart series(HeatmapCalendarSeriesOpts seriesOpts, String dataColumn) {
        option.series(seriesOpts, Index.of(
                Objects.requireNonNull(dataColumn, "Null 'dataColumn' column")));
        return this;
    }

    /**
     * Adds a "heatmap" series to the plot with data coming from the DataFrame column passed as method arguments.
     * The plot will be done on a cartesian coordinate system and requires columns for "x", "y", and "heat" values.
     * Series configuration is specified via the {@link HeatmapCartesian2DSeriesOpts} argument.
     *
     * @since 2.0.0
     */
    public EChart series(HeatmapCartesian2DSeriesOpts seriesOpts, String xColumn, String yColumn, String heatColumn) {
        option.series(seriesOpts, Index.of(
                Objects.requireNonNull(xColumn, "Null 'xColumn' column"),
                Objects.requireNonNull(yColumn, "Null 'yColumn' column"),
                Objects.requireNonNull(heatColumn, "Null 'heatColumn' column")));
        return this;
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

    /**
     * @since 2.0.0
     */
    public EChart title(Title title) {
        option.title(title);
        return this;
    }

    public EChart legend() {
        option.legend();
        return this;
    }

    /**
     * @since 2.0.0
     */
    public EChart legend(Legend legend) {
        option.legend(legend);
        return this;
    }

    /**
     * Returns an object with chart HTML / JavaScript components. Assigns a random ID to the HTML div container
     */
    public EChartHtml plot(DataFrame dataFrame) {
        return plot(dataFrame, newId());
    }

    /**
     * Returns an object with chart HTML / JavaScript components. Assigns the specified ID to the HTML div container
     *
     * @since 2.0.0
     */
    public EChartHtml plot(DataFrame dataFrame, String divContainerId) {
        ContainerModel containerModel = new ContainerModel(
                divContainerId,
                this.width != null ? this.width : 600,
                this.height != null ? this.height : 400
        );

        ScriptModel scriptModel = new ScriptModel(
                divContainerId,
                this.theme,
                this.renderer != null ? new InitOptsModel(renderer.name()) : null,
                option.resolve(dataFrame)
        );

        return new EChartHtml(
                divContainerId,
                echartsUrl(),
                Renderer.renderContainer(containerModel),
                Renderer.renderScript(scriptModel),
                idGenerator,
                containerModel,
                scriptModel
        );
    }

    /**
     * @since 2.0.0
     */
    protected String echartsUrl() {
        return scriptUrl != null ? scriptUrl : DEFAULT_ECHARTS_SCRIPT_URL;
    }

    /**
     * @deprecated unused
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    protected String generateContainerHtml(String id) {
        ContainerModel model = new ContainerModel(
                id,
                this.width != null ? this.width : 600,
                this.height != null ? this.height : 400
        );
        return Renderer.renderContainer(model);
    }

    /**
     * @deprecated unused
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    protected String generateExternalScriptHtml() {
        return new EChartHtml(
                "-",
                scriptUrl != null ? scriptUrl : DEFAULT_ECHARTS_SCRIPT_URL,
                "",
                "",
                idGenerator,
                null,
                null
        ).getExternalScript();
    }

    /**
     * @deprecated unused
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    protected String generateScriptHtml(String id, DataFrame df) {
        return new EChartHtml(
                id,
                "",
                "",
                generateScript(id, df),
                idGenerator,
                null,
                null
        ).getScript();
    }

    /**
     * @deprecated unused
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    protected String generateScript(String id, DataFrame df) {

        ScriptModel model = new ScriptModel(
                id,
                this.theme,
                this.renderer != null ? new InitOptsModel(renderer.name()) : null,
                option.resolve(df)
        );
        return Renderer.renderScript(model);
    }

    protected String newId() {
        return idGenerator.nextId();
    }
}
