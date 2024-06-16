package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.ValueModel;
import org.dflib.echarts.render.option.DataSetModel;
import org.dflib.echarts.render.option.RowModel;
import org.dflib.echarts.render.option.SeriesModel;
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

    private List<BoundXAxis> xAxes;
    private List<YAxis> yAxes;

    private final Map<String, BoundSeries> series;
    private SeriesOpts defaultSeriesOpts;

    /**
     * @since 1.0.0-M22
     */
    public static Option of() {
        return new Option();
    }

    protected Option() {
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
     * Adds an X axis to the chart, that will use the specified DataFrame column as axis labels. If no X axis is set,
     * series element indices will be used for X.
     */
    public Option xAxis(String dataColumn) {
        return xAxis(dataColumn, XAxis.ofDefault());
    }

    public Option xAxis(XAxis axis) {
        return xAxis(null, axis);
    }

    public Option xAxis(String dataColumn, XAxis axis) {
        Objects.requireNonNull(dataColumn);

        if (xAxes == null) {
            xAxes = new ArrayList<>(3);
        }

        xAxes.add(new BoundXAxis(dataColumn, axis));
        return this;
    }


    /**
     * Adds one or more Y axes to the chart.
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

        boolean cartesianDefaults = useCartesianDefaults();
        List<XAxis> xs = xAxes != null
                ? xAxes.stream().map(BoundXAxis::getAxis).collect(Collectors.toList())
                : (cartesianDefaults ? List.of(XAxis.ofDefault()) : null);
        List<YAxis> ys = yAxes != null ? yAxes : (cartesianDefaults ? List.of(YAxis.ofDefault()) : null);
        DataSetModelMeta dataset = dataset(df, cartesianDefaults);

        return new OptionModel(
                dataset.dataset,
                this.legend != null ? this.legend : false,
                datasetSeries(dataset.labelsOffset),
                this.title,
                this.toolbox != null ? this.toolbox.resolve() : null,
                this.tooltip != null ? this.tooltip.resolve() : null,
                xs != null ? xs.stream().map(XAxis::resolve).collect(Collectors.toList()) : null,
                ys != null ? ys.stream().map(YAxis::resolve).collect(Collectors.toList()) : null
        );
    }

    protected boolean useCartesianDefaults() {
        return series.isEmpty()
                || series.values().stream().filter(BoundSeries::isCartesianOrNull).findFirst().isPresent();
    }

    protected DataSetModelMeta dataset(DataFrame df, boolean cartesianDefaults) {

        // DF columns become rows and rows become columns in the EChart dataset
        int w = df.height();
        int h = series.size();

        List<RowModel> labelRows = datasetLabelRows(df, cartesianDefaults);

        List<RowModel> rows = new ArrayList<>(h + labelRows.size());
        rows.addAll(labelRows);

        String[] rowLabels = series.keySet().toArray(new String[0]);
        for (int i = 0; i < h; i++) {
            List<ValueModel> row = new ArrayList<>(w + 1);
            row.add(new ValueModel(rowLabels[i], w == 0));
            Series<?> data = df.getColumn(rowLabels[i]);

            for (int j = 0; j < w; j++) {
                row.add(new ValueModel(data.get(j), j + 1 == w));
            }

            rows.add(new RowModel(row, i + 1 == h));
        }

        return new DataSetModelMeta(new DataSetModel(rows), labelRows.size());
    }

    protected List<RowModel> datasetLabelRows(DataFrame df, boolean cartesianDefaults) {

        Map<String, List<ValueModel>> rowMap = new LinkedHashMap<>();

        // the first source of labels - a column associated with XAxis
        if (xAxes != null) {

            for (BoundXAxis a : xAxes) {
                List<ValueModel> xAxisLabels = datasetLabelRow(df, a.columnName);
                rowMap.put((String) xAxisLabels.get(0).getValue(), xAxisLabels);
            }

        } else if (cartesianDefaults) {
            List<ValueModel> xAxisLabels = datasetLabelRow(df, null);
            rowMap.put((String) xAxisLabels.get(0).getValue(), xAxisLabels);
        }

        // the next source of labels - columns associated with pie charts
        for (BoundSeries s : series.values()) {

            if (s.opts instanceof PieSeriesOpts) {
                PieSeriesOpts pco = (PieSeriesOpts) s.opts;

                List<ValueModel> pieLabels = datasetLabelRow(df, pco.getLabelColumn());
                String key = (String) pieLabels.get(0).getValue();

                rowMap.putIfAbsent(key, pieLabels);
            }
        }

        int len = rowMap.size();
        List<RowModel> rows = new ArrayList<>(len);

        int[] i = new int[1];
        rowMap.forEach((k, v) -> rows.add(new RowModel(v, i[0]++ == len)));

        return rows;
    }

    protected List<ValueModel> datasetLabelRow(DataFrame df, String columnName) {

        // DF columns become rows and rows become columns in the EChart dataset
        int w = df.height();

        List<ValueModel> row = new ArrayList<>(w + 1);

        Series<?> columnLabels = columnName != null
                ? df.getColumn(columnName)
                : new IntSequenceSeries(1, df.height() + 1);

        String labelsName = columnName != null ? columnName : "labels";
        row.add(new ValueModel(labelsName, w == 0));
        for (int i = 0; i < w; i++) {
            row.add(new ValueModel(columnLabels.get(i), i + 1 == w));
        }

        return row;
    }

    protected List<SeriesModel> datasetSeries(int labelsOffset) {
        SeriesOpts baseOpts = baseSeriesOptsTemplate();
        int len = series.size();
        int i = labelsOffset;

        List<SeriesModel> models = new ArrayList<>(len);
        for (BoundSeries s : series.values()) {

            // TODO: other cases when labelsPos != 0 ?? (pie charts?)

            Integer labelsPos = (s.opts instanceof CartesianSeriesOpts) ? ((CartesianSeriesOpts) s.opts).xAxisIndex : null;
            SeriesModel m = s.fillOpts(baseOpts).resolve(labelsPos != null ? labelsPos : 0, i++);
            models.add(m);
        }

        return models;
    }

    protected SeriesOpts baseSeriesOptsTemplate() {
        return this.defaultSeriesOpts != null ? defaultSeriesOpts : SeriesOpts.ofLine();
    }

    static class DataSetModelMeta {
        final int labelsOffset;
        final DataSetModel dataset;

        DataSetModelMeta(DataSetModel dataset, int labelsOffset) {
            this.dataset = dataset;
            this.labelsOffset = labelsOffset;
        }
    }

    static class BoundXAxis {
        final String columnName;
        final XAxis axis;

        BoundXAxis(String columnName, XAxis axis) {
            this.columnName = columnName;
            this.axis = axis;
        }

        XAxis getAxis() {
            return axis;
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

        boolean isCartesianOrNull() {
            return opts == null || opts instanceof CartesianSeriesOpts;
        }

        SeriesModel resolve(int labelsPos, int seriesPos) {
            return opts.resolve(
                    columnName,
                    labelsPos,
                    seriesPos,
                    // hardcoding "row" series layout. It corresponds to the dataset layout
                    "row"
            );
        }
    }
}
