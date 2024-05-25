package org.dflib.echarts;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.model.AxisLabelModel;
import org.dflib.echarts.model.AxisModel;
import org.dflib.echarts.model.ContainerModel;
import org.dflib.echarts.model.ExternalScriptModel;
import org.dflib.echarts.model.ScriptModel;
import org.dflib.echarts.model.SeriesModel;
import org.dflib.echarts.model.ValueModel;
import org.dflib.echarts.model.ValuePairModel;
import org.dflib.series.IntSequenceSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * A builder of HTML/JS code that renders DataFrame data using ECharts library.
 *
 * @since 1.0.0-M21
 */
public class EChartBuilder {
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

    // TODO: extract "RootOpts" or "Opts" from the builder similar to SeriesOpts, etc.

    private final Random rnd;
    private String theme;

    private String scriptUrl;
    private String title;
    private Integer width;
    private Integer height;
    private Boolean legend;

    private String xAxisColumn;
    private AxisOpts xAxisOpts;

    private AxisOpts yAxisOpts;

    private final Map<String, SeriesOpts> series;
    private final Map<StringPair, SeriesOpts> timeSeries;

    private final SeriesOpts defaultSeriesOpts;
    private SeriesOpts seriesOpts;

    protected EChartBuilder(SeriesOpts defaultSeriesOpts) {
        this.rnd = new SecureRandom();

        this.defaultSeriesOpts = defaultSeriesOpts;

        // keeping the "series" order predictable
        this.series = new LinkedHashMap<>();
        this.timeSeries = new LinkedHashMap<>();
    }

    /**
     * Specifies which DataFrame column should be used to label the X axis. This setting is optional. If not set,
     * series element indices will be used for X.
     */
    public EChartBuilder xAxis(String xAxisColumn) {
        this.xAxisColumn = xAxisColumn;
        return this;
    }

    public EChartBuilder xAxis(String xAxisColumn, AxisOpts opts) {
        this.xAxisColumn = Objects.requireNonNull(xAxisColumn);
        this.xAxisOpts = Objects.requireNonNull(opts);
        return this;
    }

    public EChartBuilder xAxisOpts(AxisOpts opts) {
        this.xAxisOpts = Objects.requireNonNull(opts);
        return this;
    }

    public EChartBuilder yAxisOpts(AxisOpts opts) {
        this.yAxisOpts = Objects.requireNonNull(opts);
        return this;
    }

    /**
     * Alters the default series options that will be used by all data series that don't have explicit options.
     */
    public EChartBuilder seriesOpts(SeriesOpts opts) {
        this.seriesOpts = opts;
        return this;
    }

    /**
     * Specifies a DataFrame column that should be plotted as a single data "series". Specifies option overrides
     * for this series.
     */
    public EChartBuilder series(String dataColumn, SeriesOpts opts) {
        series.put(dataColumn, opts);
        return this;
    }

    /**
     * Specifies which DataFrame columns should be plotted as data "series". Unless you override it later,
     * all series will be rendered with the default chart options.
     */
    public EChartBuilder series(String... dataColumns) {
        for (String c : dataColumns) {

            // can't use "computeIfAbsent", as it doesn't handle null properly
            if (!series.containsKey(c)) {
                series.put(c, null);
            }
        }

        return this;
    }


    public EChartBuilder timeSeries(String timeColumn, String dataColumn) {
        StringPair key = new StringPair(timeColumn, dataColumn);

        // can't use "computeIfAbsent", as it doesn't handle null properly
        if (!timeSeries.containsKey(key)) {
            timeSeries.put(key, null);
        }

        return this;
    }

    public EChartBuilder timeSeries(String timeColumn, String dataColumn, SeriesOpts opts) {
        timeSeries.put(new StringPair(timeColumn, dataColumn), opts);
        return this;
    }

    /**
     * Sets an alternative URL of ECharts JavaScript source. If not set, the default public URL is provided.
     */
    public EChartBuilder scriptUrl(String url) {
        this.scriptUrl = url;
        return this;
    }

    public EChartBuilder title(String title) {
        this.title = title;
        return this;
    }

    public EChartBuilder legend() {
        this.legend = Boolean.TRUE;
        return this;
    }

    public EChartBuilder sizePixels(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * @see #darkTheme()
     */
    public EChartBuilder theme(String theme) {
        this.theme = theme;
        return this;
    }

    public EChartBuilder darkTheme() {
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

        if (!series.isEmpty() && !timeSeries.isEmpty()) {
            throw new IllegalStateException("Both 'series' and 'timeSeries' are set. Only one of them can be present");
        }

        boolean isTimeSeries = !timeSeries.isEmpty();

        ScriptModel model = new ScriptModel(
                id,
                this.title,
                xAxis(df, isTimeSeries),
                yAxis(),
                isTimeSeries ? dataSeriesFromTimeSeries(df) : dataSeriesFromSeries(df),
                this.theme,
                this.legend != null ? this.legend : false
        );
        return SCRIPT_TEMPLATE.execute(new StringWriter(), model).toString();
    }

    protected String newId() {
        return "dfl_ech_" + Math.abs(rnd.nextInt(10_000));
    }

    protected AxisModel xAxis(DataFrame df, boolean isTimeSeries) {

        AxisOpts xAxis = this.xAxisOpts != null ? this.xAxisOpts : (isTimeSeries ? AxisOpts.of(AxisType.time) : AxisOpts.defaultX());

        // TODO: "getColumn" here would throw on invalid column name, while "cols" in "dataSeries" will
        //  create an empty column... An inconsistency?

        Series<?> xSeries = this.xAxisColumn != null
                ? df.getColumn(this.xAxisColumn)
                : new IntSequenceSeries(1, df.height() + 1);

        return new AxisModel(
                xAxis.getType().name(),
                xAxis.getLabel() != null ? new AxisLabelModel(xAxis.getLabel().getFormatter()) : null,
                xAxis.isBoundaryGap(),
                xSeries
        );
    }

    protected AxisModel yAxis() {

        AxisOpts yAxis = this.yAxisOpts != null ? this.yAxisOpts : AxisOpts.defaultY();

        return new AxisModel(
                yAxis.getType().name(),
                yAxis.getLabel() != null ? new AxisLabelModel(yAxis.getLabel().getFormatter()) : null,
                yAxis.isBoundaryGap(),
                null
        );
    }

    protected List<SeriesModel> dataSeriesFromSeries(DataFrame df) {
        SeriesOpts defaultOpts = defaultSeriesOpts();
        String[] columns = series.keySet().toArray(new String[0]);

        int len = columns.length;

        List<SeriesModel> models = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {

            SeriesOpts columnOpts = series.get(columns[i]);
            SeriesOpts mergedOpts = columnOpts != null ? defaultOpts.merge(columnOpts) : defaultOpts;

            SeriesModel m = new SeriesModel(
                    columns[i],
                    seriesData(df.getColumn(columns[i])),
                    null,
                    mergedOpts.getType().name(),
                    mergedOpts.isAreaStyle(),
                    mergedOpts.isStack(),
                    mergedOpts.isSmooth(),
                    i + 1 == len
            );

            models.add(m);
        }
        return models;
    }

    protected List<SeriesModel> dataSeriesFromTimeSeries(DataFrame df) {

        SeriesOpts defaultOpts = defaultSeriesOpts();
        StringPair[] columnPairs = timeSeries.keySet().toArray(new StringPair[0]);
        int len = columnPairs.length;

        List<SeriesModel> models = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {

            SeriesOpts columnOpts = timeSeries.get(columnPairs[i]);
            SeriesOpts mergedOpts = columnOpts != null ? defaultOpts.merge(columnOpts) : defaultOpts;

            SeriesModel m = new SeriesModel(
                    columnPairs[i].right,
                    null,
                    timeSeriesData(df.getColumn(columnPairs[i].left), df.getColumn(columnPairs[i].right)),
                    mergedOpts.getType().name(),
                    mergedOpts.isAreaStyle(),
                    mergedOpts.isStack(),
                    mergedOpts.isSmooth(),
                    i + 1 == len
            );

            models.add(m);
        }
        return models;
    }

    protected SeriesOpts defaultSeriesOpts() {
        return this.seriesOpts != null
                ? this.defaultSeriesOpts.merge(this.seriesOpts)
                : this.defaultSeriesOpts;
    }

    protected List<ValueModel> seriesData(Series<?> series) {
        int len = series.size();
        List<ValueModel> data = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            data.add(new ValueModel(series.get(i), i + 1 == len));
        }

        return data;
    }

    protected List<ValuePairModel> timeSeriesData(Series<?> time, Series<?> values) {
        int len = values.size();
        List<ValuePairModel> data = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            data.add(new ValuePairModel(time.get(i), values.get(i), i + 1 == len));
        }

        return data;
    }

    static class StringPair {
        final String left;
        final String right;

        StringPair(String left, String right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringPair that = (StringPair) o;
            return Objects.equals(left, that.left) && Objects.equals(right, that.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }
}
