package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.ValueModel;
import org.dflib.echarts.render.option.GridModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.dataset.DatasetModel;
import org.dflib.echarts.render.option.dataset.DatasetRowModel;
import org.dflib.series.IntSequenceSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class OpToOptionModel {

    private static final String DEFAULT_LABELS_LABEL = "labels";

    private final Option opt;
    private final DataFrame dataFrame;

    OpToOptionModel(Option opt, DataFrame dataFrame) {
        this.opt = Objects.requireNonNull(opt);
        this.dataFrame = Objects.requireNonNull(dataFrame);
    }

    OptionModel resolve() {

        List<OptionSeriesBuilder> series = dedupeSeriesNames();

        boolean cartesianDefaults = useCartesianDefaults(series);
        List<XAxis> xs = opt.xAxes != null
                ? opt.xAxes.stream().map(Option.BoundXAxis::getAxis).collect(Collectors.toList())
                : (cartesianDefaults ? List.of(XAxis.ofDefault()) : null);
        List<YAxis> ys = opt.yAxes != null ? opt.yAxes : (cartesianDefaults ? List.of(YAxis.ofDefault()) : null);
        DataSetLabels labels = datasetLabels(series, cartesianDefaults);
        DatasetModel dataset = dataset(series, labels.rows);
        List<SeriesModel> seriesModels = seriesModels(series, labels);

        return new OptionModel(
                dataset,
                opt.legend != null ? opt.legend : false,
                grids(),
                seriesModels,
                opt.title,
                opt.toolbox != null ? opt.toolbox.resolve() : null,
                opt.tooltip != null ? opt.tooltip.resolve() : null,
                xs != null ? xs.stream().map(XAxis::resolve).collect(Collectors.toList()) : null,
                ys != null ? ys.stream().map(YAxis::resolve).collect(Collectors.toList()) : null
        );
    }

    private List<OptionSeriesBuilder> dedupeSeriesNames() {

        if (opt.series.size() < 2) {
            return opt.series;
        }

        UnaryOperator<OptionSeriesBuilder> nameDeduplicator = new UnaryOperator<>() {
            final Set<String> names = new HashSet<>();
            int counter;

            @Override
            public OptionSeriesBuilder apply(OptionSeriesBuilder sb) {
                String startName = sb.name;

                // change in-place... OptionSeriesBuilder is not externally visible
                while (!names.add(sb.name)) {
                    sb.name(startName + "_" + counter++);
                }

                return sb;
            }
        };

        return opt.series.stream().map(nameDeduplicator::apply).collect(Collectors.toList());
    }

    private List<GridModel> grids() {
        return opt.grids != null
                ? opt.grids.stream().map(Grid::resolve).collect(Collectors.toList())
                : null;
    }

    protected boolean useCartesianDefaults(List<OptionSeriesBuilder> series) {
        return series.isEmpty()
                || series.stream().filter(sb -> sb.opts.getType().isCartesian()).findFirst().isPresent();
    }

    protected DataSetLabels datasetLabels(List<OptionSeriesBuilder> series, boolean cartesianDefaults) {

        Map<String, List<ValueModel>> rowMap = new LinkedHashMap<>();

        Map<Integer, Integer> rowPosByXAxisIndex = new HashMap<>();
        Map<String, Integer> rowPosByRowLabel = new HashMap<>();

        // The first source of labels - a column associated with XAxis.
        if (opt.xAxes != null) {

            // If multiple Axis point to the same data column, we'll generate only one dataset row for it
            int xlen = opt.xAxes.size();
            for (int i = 0; i < xlen; i++) {
                String columnName = opt.xAxes.get(i).columnName;
                String labelsName = labelsName(columnName);

                Integer existingPos = rowPosByRowLabel.get(labelsName);
                if (existingPos != null) {
                    rowPosByXAxisIndex.put(i, existingPos);
                } else {
                    rowPosByRowLabel.put(labelsName, i);
                    rowPosByXAxisIndex.put(i, i);
                    rowMap.put(labelsName, datasetLabelRow(columnName, labelsName));
                }
            }

        } else if (cartesianDefaults) {
            String labelsName = labelsName(null);
            List<ValueModel> xAxisLabels = datasetLabelRow(null, labelsName);
            rowMap.put((String) xAxisLabels.get(0).getValue(), xAxisLabels);
        }

        // the next source of labels - columns associated with pie charts
        for (OptionSeriesBuilder sb : series) {

            if (sb.opts instanceof PieSeriesOpts) {
                PieSeriesOpts pco = (PieSeriesOpts) sb.opts;
                String labelsName = labelsName(pco.getLabelColumn());
                List<ValueModel> pieLabels = datasetLabelRow(pco.getLabelColumn(), labelsName);
                String key = (String) pieLabels.get(0).getValue();

                rowMap.putIfAbsent(key, pieLabels);
            }
        }

        int len = rowMap.size();
        List<DatasetRowModel> rows = new ArrayList<>(len);

        int[] i = new int[1];
        rowMap.forEach((k, v) -> rows.add(new DatasetRowModel(v, i[0]++ == len)));

        return new DataSetLabels(rowPosByXAxisIndex, rows);
    }

    private List<SeriesModel> seriesModels(List<OptionSeriesBuilder> series, DataSetLabels labels) {

        OpToSeriesModel resolver = new OpToSeriesModel(
                labels.xAxisIndices,

                // hardcoding "row" series layout. It corresponds to the dataset layout created elsewhere in this object
                "row",

                // Series data rows follow label rows. So apply the offset to the "seriesPos" of the encoder
                labels.rows.size()
        );

        List<SeriesModel> models = new ArrayList<>(series.size());
        for (OptionSeriesBuilder sb : series) {
            models.add(resolver.resolve(sb.opts, sb.name));
        }

        return models;
    }

    protected List<ValueModel> datasetLabelRow(String columnName, String labelsName) {

        // DF columns become rows and rows become columns in the EChart dataset
        int w = dataFrame.height();

        List<ValueModel> row = new ArrayList<>(w + 1);

        Series<?> columnLabels = columnName != null
                ? dataFrame.getColumn(columnName)
                : new IntSequenceSeries(1, dataFrame.height() + 1);

        row.add(new ValueModel(labelsName, w == 0));
        for (int i = 0; i < w; i++) {
            row.add(new ValueModel(columnLabels.get(i), i + 1 == w));
        }

        return row;
    }

    protected String labelsName(String columnName) {
        return columnName != null ? columnName : DEFAULT_LABELS_LABEL;
    }

    protected DatasetModel dataset(List<OptionSeriesBuilder> series, List<DatasetRowModel> labels) {

        List<List<ValueModel>> rows = new ArrayList<>(series.size());

        // DF columns become rows and rows become columns in the EChart dataset
        int w = dataFrame.height();

        for (OptionSeriesBuilder sb : series) {

            List<ValueModel> row = new ArrayList<>(w + 1);

            // for now, only support "dataset" for single column series.
            // TODO: multi-column support for series like "candlestick" or "boxplot"
            if (sb.dataColumns.size() > 1) {
                String chartType = sb.opts.getClass().getSimpleName();
                throw new UnsupportedOperationException("'dataset' generation is not supported for multi-column charts type: " + chartType);
            }

            String rowLabel = sb.dataColumns.get(0);
            row.add(new ValueModel(rowLabel, w == 0));
            Series<?> data = dataFrame.getColumn(rowLabel);

            for (int j = 0; j < w; j++) {
                row.add(new ValueModel(data.get(j), j + 1 == w));
            }

            rows.add(row);
        }

        if (rows.isEmpty()) {
            return null;
        }

        int h = rows.size();
        List<DatasetRowModel> rowModels = new ArrayList<>(h + labels.size());
        rowModels.addAll(labels);
        for (int i = 0; i < h; i++) {
            rowModels.add(new DatasetRowModel(rows.get(i), i + 1 == h));
        }

        return new DatasetModel(rowModels);
    }

    static class DataSetLabels {
        final List<DatasetRowModel> rows;
        final Map<Integer, Integer> xAxisIndices;

        DataSetLabels(Map<Integer, Integer> xAxisIndices, List<DatasetRowModel> rows) {
            this.xAxisIndices = xAxisIndices;
            this.rows = rows;
        }
    }
}
