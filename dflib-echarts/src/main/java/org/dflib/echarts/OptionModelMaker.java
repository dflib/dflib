package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.option.GridModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.series.IntSequenceSeries;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class OptionModelMaker {

    private final Option opt;
    private final DataFrame dataFrame;

    OptionModelMaker(Option opt, DataFrame dataFrame) {
        this.opt = Objects.requireNonNull(opt);
        this.dataFrame = Objects.requireNonNull(dataFrame);
    }

    OptionModel resolve() {

        List<SeriesBuilder<?>> series = dedupeSeriesNames();
        boolean cartesianDefaults = useCartesianDefaults(series);

        List<XAxisBuilder> xs = opt.xAxes != null
                ? opt.xAxes
                : (cartesianDefaults ? List.of(new XAxisBuilder(null, XAxis.ofDefault())) : null);
        List<YAxis> ys = opt.yAxes != null ? opt.yAxes : (cartesianDefaults ? List.of(YAxis.ofDefault()) : null);

        DatasetBuilder dsb = dataset(series, xs);

        dsb.linkSeriesToRows(series);

        return new OptionModel(
                dsb.datasetModel(),
                opt.legend != null ? opt.legend.resolve() : null,
                grids(),
                seriesModels(series),
                opt.title != null ? opt.title.resolve() : null,
                opt.toolbox != null ? opt.toolbox.resolve() : null,
                opt.tooltip != null ? opt.tooltip.resolve() : null,
                xs != null ? xs.stream().map(XAxisBuilder::getAxis).map(XAxis::resolve).collect(Collectors.toList()) : null,
                ys != null ? ys.stream().map(YAxis::resolve).collect(Collectors.toList()) : null
        );
    }

    private List<SeriesBuilder<?>> dedupeSeriesNames() {

        if (opt.series.size() < 2) {
            return opt.series;
        }

        UnaryOperator<SeriesBuilder<?>> nameDeduplicator = new UnaryOperator<>() {
            final Set<String> names = new HashSet<>();
            int counter;

            @Override
            public SeriesBuilder<?> apply(SeriesBuilder<?> sb) {
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

    List<SeriesModel> seriesModels(List<SeriesBuilder<?>> series) {
        if (series.isEmpty()) {
            return null;
        }

        SeriesModelMaker resolver = new SeriesModelMaker();
        return series.stream().map(resolver::resolve).collect(Collectors.toList());
    }

    private boolean useCartesianDefaults(List<SeriesBuilder<?>> series) {
        return series.isEmpty()
                || series.stream().anyMatch(sb -> sb.seriesOpts.getCoordinateSystemType().isCartesian());
    }

    private DatasetBuilder dataset(List<SeriesBuilder<?>> series, List<XAxisBuilder> xs) {
        DatasetBuilder dsb = new DatasetBuilder(dataFrame);
        appendXAxesLabels(dsb, xs);
        appendPieChartLabels(dsb, series);
        appendDatasetRows(dsb, series);

        return dsb;
    }

    private void appendXAxesLabels(DatasetBuilder dsb, List<XAxisBuilder> xs) {

        if (xs != null) {
            int len = xs.size();
            for (int i = 0; i < len; i++) {
                XAxisBuilder ab = xs.get(i);
                if(ab.columnName != null) {
                    dsb.append(dataFrame.getColumn(ab.columnName), DatasetBuilder.DatasetRowType.xAxisLabels, i);
                }
                else {
                    // TODO: appending X data as a Series to prevent column reuse which is not possible until
                    //  https://github.com/apache/echarts/issues/20330 is fixed
                    dsb.append(new IntSequenceSeries(1, dataFrame.height() + 1), DatasetBuilder.DatasetRowType.xAxisLabels, i);
                }
            }
        }
    }

    private void appendPieChartLabels(DatasetBuilder dsb, List<SeriesBuilder<?>> series) {

        int len = series.size();
        for (int i = 0; i < len; i++) {

            SeriesOpts<?> opts = series.get(i).seriesOpts;

            if (opts instanceof PieSeriesOpts) {
                PieSeriesOpts pco = (PieSeriesOpts) opts;
                if (pco.getLabelColumn() != null) {
                    dsb.append(dataFrame.getColumn(pco.getLabelColumn()), DatasetBuilder.DatasetRowType.pieItemName, i);
                } else {
                    // TODO: appending pie label data as a Series to prevent column reuse which is not possible until
                    //   https://github.com/apache/echarts/issues/20330 is fixed
                    dsb.append(new IntSequenceSeries(1, dataFrame.height() + 1), DatasetBuilder.DatasetRowType.pieItemName, i);
                }
            }
        }
    }

    // updates both "dsb" (new dataset rows) and "series" (indices of dataset rows)
    private void appendDatasetRows(DatasetBuilder dsb, List<SeriesBuilder<?>> series) {
        int len = series.size();
        for (int i = 0; i < len; i++) {

            SeriesBuilder<?> sb = series.get(i);
            if (sb.dataColumns != null) {
                for (String dc : sb.dataColumns) {
                    dsb.append(dc, DatasetBuilder.DatasetRowType.seriesData, i);
                }
            }
        }
    }
}
