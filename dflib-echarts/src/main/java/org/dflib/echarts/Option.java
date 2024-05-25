package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.render.AxisLabelModel;
import org.dflib.echarts.render.AxisModel;
import org.dflib.echarts.render.DataSetModel;
import org.dflib.echarts.render.EncodeModel;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.RowModel;
import org.dflib.echarts.render.SeriesModel;
import org.dflib.echarts.render.ToolboxModel;
import org.dflib.echarts.render.ValueModel;
import org.dflib.echarts.render.toolbox.FeatureDataZoomModel;
import org.dflib.echarts.render.toolbox.FeatureRestoreModel;
import org.dflib.echarts.render.toolbox.FeatureSaveAsImageModel;
import org.dflib.series.IntSequenceSeries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A builder of the EChart "option" object - the main chart configuration.
 *
 * @since 1.0.0-M21
 */
public class Option {
    private String title;
    private Boolean legend;
    private Toolbox toolbox;

    private String xAxisColumn;
    private Axis xAxis;
    private Axis yAxis;

    private final Map<String, SeriesOpts> series;
    private final SeriesOpts defaultSeriesOpts;
    private SeriesOpts seriesOpts;

    public Option(SeriesOpts defaultSeriesOpts) {
        this.defaultSeriesOpts = defaultSeriesOpts;

        // keeping the "series" order predictable
        this.series = new LinkedHashMap<>();
    }

    public Option toolbox(Toolbox toolbox) {
        this.toolbox = Objects.requireNonNull(toolbox);
        return this;
    }

    /**
     * Specifies which DataFrame column should be used to label the X axis. This setting is optional. If not set,
     * series element indices will be used for X.
     */
    public Option xAxis(String dataColumn) {
        this.xAxisColumn = dataColumn;
        return this;
    }

    public Option xAxis(String dataColumn, Axis axis) {
        this.xAxisColumn = Objects.requireNonNull(dataColumn);
        this.xAxis = Objects.requireNonNull(axis);
        return this;
    }

    public Option xAxis(Axis axis) {
        this.xAxis = Objects.requireNonNull(axis);
        return this;
    }

    public Option yAxis(Axis axis) {
        this.yAxis = Objects.requireNonNull(axis);
        return this;
    }

    /**
     * Alters the default series options that will be used by all data series that don't have explicit options.
     */
    public Option seriesOpts(SeriesOpts opts) {
        this.seriesOpts = opts;
        return this;
    }

    /**
     * Specifies a DataFrame column that should be plotted as a single data "series". Specifies option overrides
     * for this series.
     */
    public Option series(String dataColumn, SeriesOpts opts) {
        series.put(dataColumn, opts);
        return this;
    }

    /**
     * Specifies which DataFrame columns should be plotted as data "series". Unless you override it later,
     * all series will be rendered with the default chart options.
     */
    public Option series(String... dataColumns) {
        for (String c : dataColumns) {

            // can't use "computeIfAbsent", as it doesn't handle null properly
            if (!series.containsKey(c)) {
                series.put(c, null);
            }
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

    public OptionModel resolve(DataFrame df) {
        return new OptionModel(
                this.title,
                toolbox(),
                dataset(df),
                xAxis(),
                yAxis(),
                seriesFromSeries(),
                this.legend != null ? this.legend : false
        );
    }

    protected ToolboxModel toolbox() {

        if (toolbox == null) {
            return null;
        }

        return new ToolboxModel(
                toolbox.isFeatureDataZoom() ? new FeatureDataZoomModel() : null,
                toolbox.isFeatureSaveAsImage() ? new FeatureSaveAsImageModel() : null,
                toolbox.isFeatureRestore() ? new FeatureRestoreModel() : null
        );
    }

    protected DataSetModel dataset(DataFrame df) {

        // DF columns become rows and rows become columns in the EChart dataset
        int w = df.height();
        int h = series.size();

        List<ValueModel> headerRow = new ArrayList<>(w + 1);
        List<RowModel> rows = new ArrayList<>(h + 1);

        String[] rowLabels = series.keySet().toArray(new String[0]);


        Series<?> columnLabels = this.xAxisColumn != null
                ? df.getColumn(this.xAxisColumn)
                : new IntSequenceSeries(1, w + 1);
        String columnLabelsLabel = this.xAxisColumn != null ? this.xAxisColumn : "labels";

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

    protected AxisModel xAxis() {

        Axis xAxis = this.xAxis != null ? this.xAxis : Axis.defaultX();

        return new AxisModel(
                xAxis.getType().name(),
                xAxis.getLabel() != null ? new AxisLabelModel(xAxis.getLabel().getFormatter()) : null,
                xAxis.isBoundaryGap()
        );
    }

    protected AxisModel yAxis() {

        Axis yAxis = this.yAxis != null ? this.yAxis : Axis.defaultY();

        return new AxisModel(
                yAxis.getType().name(),
                yAxis.getLabel() != null ? new AxisLabelModel(yAxis.getLabel().getFormatter()) : null,
                yAxis.isBoundaryGap()
        );
    }

    protected List<SeriesModel> seriesFromSeries() {
        SeriesOpts defaultOpts = defaultSeriesOpts();
        String[] columns = series.keySet().toArray(new String[0]);

        int len = columns.length;

        List<SeriesModel> models = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {

            SeriesOpts columnOpts = series.get(columns[i]);
            SeriesOpts mergedOpts = columnOpts != null ? defaultOpts.merge(columnOpts) : defaultOpts;

            SeriesModel m = new SeriesModel(
                    columns[i],
                    mergedOpts.getType().name(),
                    new EncodeModel(0, i + 1),
                    // hardcoding "row" series layout. It corresponds to the dataset layout
                    SeriesLayoutType.row.name(),
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

}
