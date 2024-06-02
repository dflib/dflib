package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.ValueModel;
import org.dflib.echarts.render.option.DataSetModel;
import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.RowModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.axis.AxisModel;
import org.dflib.series.IntSequenceSeries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A builder of the EChart "option" object - the main chart configuration.
 *
 * @since 1.0.0-M21
 */
public class Option {

    private String title;
    private Boolean legend;
    private Toolbox toolbox;
    private Tooltip tooltip;

    private BoundXAxis xAxis;
    private List<YAxis> yAxes;

    private final Map<String, BoundSeries> series;
    private SeriesOpts defaultSeriesOpts;

    public Option() {
        // keeping the "series" order predictable
        this.series = new LinkedHashMap<>();
    }

    public Option toolbox(Toolbox toolbox) {
        this.toolbox = toolbox;
        return this;
    }

    /**
     * @since 1.0.0-M22
     */
    public Option tooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    /**
     * Specifies which DataFrame column should be used to label the X axis. This setting is optional. If not set,
     * series element indices will be used for X.
     */
    public Option xAxis(String dataColumn) {
        this.xAxis = new BoundXAxis(dataColumn, XAxis.ofDefault());
        return this;
    }

    public Option xAxis(String dataColumn, XAxis axis) {
        this.xAxis = new BoundXAxis(dataColumn, axis);
        return this;
    }

    public Option xAxis(XAxis axis) {
        this.xAxis = new BoundXAxis(null, axis);
        return this;
    }

    /**
     * Adds one or more Y axis to the chart.
     *
     * @since 1.0.0-M22
     */
    public Option yAxes(YAxis... axes) {
        for (YAxis a : axes) {
            yAxis(a);
        }

        return this;
    }

    /**
     * Adds a Y axis to the chart.
     */
    public Option yAxis(YAxis axis) {
        Objects.requireNonNull(axis);

        if (this.yAxes == null) {
            this.yAxes = new ArrayList<>(3);
        }

        this.yAxes.add(axis);
        return this;
    }

    /**
     * Sets a template to be used with all data series that don't have their own explicit options.
     */
    public Option defaultSeriesOpts(SeriesOpts opts) {
        this.defaultSeriesOpts = opts;
        return this;
    }

    /**
     * Specifies one or more DataFrame columns to be plotted as individual series. Sets series options.
     */
    public Option series(SeriesOpts opts, String... dataColumns) {
        for (String c : dataColumns) {
            series.put(c, new BoundSeries(c, opts));
        }

        return this;
    }

    /**
     * Specifies one or more DataFrame columns to be plotted as individual series. Series will be rendered with default
     * options.
     */
    public Option series(String... dataColumns) {
        for (String c : dataColumns) {
            series.put(c, new BoundSeries(c, null));
        }

        return this;
    }

    public Option title(String title) {
        this.title = title;
        return this;
    }

    public Option legend() {
        this.legend = Boolean.TRUE;
        return this;
    }

    protected OptionModel resolve(DataFrame df) {

        BoundXAxis x = xAxis != null ? xAxis : new BoundXAxis(null, XAxis.ofDefault());
        List<YAxis> ys = yAxes != null ? yAxes : List.of(YAxis.ofDefault());
        List<AxisModel> yModels = ys.stream().map(YAxis::resolve).collect(Collectors.toList());

        return new OptionModel(
                dataset(df, x),
                this.legend != null ? this.legend : false,
                datasetSeries(),
                this.title,
                this.toolbox != null ? this.toolbox.resolve() : null,
                this.tooltip != null ? this.tooltip.resolve() : null,
                x.axis.resolve(),
                yModels
        );
    }

    protected DataSetModel dataset(DataFrame df, BoundXAxis x) {

        // DF columns become rows and rows become columns in the EChart dataset
        int w = df.height();
        int h = series.size();

        List<ValueModel> headerRow = new ArrayList<>(w + 1);
        List<RowModel> rows = new ArrayList<>(h + 1);

        String[] rowLabels = series.keySet().toArray(new String[0]);
        Series<?> columnLabels = x.resolve(df);
        String columnLabelsLabel = x.resolveLabel();

        headerRow.add(new ValueModel(columnLabelsLabel, w == 0));
        for (int i = 0; i < w; i++) {
            headerRow.add(new ValueModel(columnLabels.get(i), i + 1 == w));
        }

        rows.add(new RowModel(headerRow, h == 0));

        for (int i = 0; i < h; i++) {
            List<ValueModel> row = new ArrayList<>(w + 1);
            row.add(new ValueModel(rowLabels[i], w == 0));
            Series<?> data = df.getColumn(rowLabels[i]);

            for (int j = 0; j < w; j++) {
                row.add(new ValueModel(data.get(j), j + 1 == w));
            }

            rows.add(new RowModel(row, i + 1 == h));
        }

        return new DataSetModel(rows);
    }

    protected List<SeriesModel> datasetSeries() {
        SeriesOpts baseOpts = baseSeriesOptsTemplate();
        int len = series.size();
        int i = 0;

        List<SeriesModel> models = new ArrayList<>(len);
        for (BoundSeries s : series.values()) {
            SeriesModel m = s.fillOpts(baseOpts).resolve(i++, len);
            models.add(m);
        }

        return models;
    }

    protected SeriesOpts baseSeriesOptsTemplate() {
        return this.defaultSeriesOpts != null ? defaultSeriesOpts : SeriesOpts.line();
    }

    static class BoundXAxis {
        final String columnName;
        final XAxis axis;

        BoundXAxis(String columnName, XAxis axis) {
            this.columnName = columnName;
            this.axis = axis;
        }

        Series<?> resolve(DataFrame df) {
            return columnName != null
                    ? df.getColumn(columnName)
                    : new IntSequenceSeries(1, df.height() + 1);
        }

        String resolveLabel() {
            return columnName != null ? columnName : "labels";
        }
    }

    static class BoundSeries {
        final String columnName;
        final SeriesOpts opts;

        BoundSeries(String columnName, SeriesOpts opts) {
            this.columnName = Objects.requireNonNull(columnName);
            this.opts = opts;
        }

        BoundSeries fillOpts(SeriesOpts defaultOpts) {
            return opts != null ? this : new BoundSeries(columnName, defaultOpts);
        }

        SeriesModel resolve(int seriesNum, int totalSeries) {
            return opts.resolve(
                    columnName,
                    new EncodeModel(0, seriesNum + 1),
                    // hardcoding "row" series layout. It corresponds to the dataset layout
                    "row",
                    seriesNum + 1 == totalSeries
            );
        }
    }
}
