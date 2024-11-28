package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.option.GridModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.series.IntSequenceSeries;

import java.util.ArrayList;
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

        DatasetBuilder dsb = new DatasetBuilder(dataFrame);
        appendXAxesLabels(dsb, series, xs);
        appendPieChartLabels(dsb, series);
        appendDatasetRows(dsb, series);

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

    // updates both "dsb" (new dataset rows) and "xs" (label indices)
    private void appendXAxesLabels(DatasetBuilder dsb, List<SeriesBuilder<?>> series, List<XAxisBuilder> xs) {

        if (xs != null) {
            int len = xs.size();
            for (int i = 0; i < len; i++) {
                XAxisBuilder ab = xs.get(i);
                int pos = ab.columnName != null
                        // TODO: appending X data as a Series to prevent column reuse which is not possible until
                        //  https://github.com/apache/echarts/issues/20330 is fixed
                        ? dsb.appendRow(dataFrame.getColumn(ab.columnName))
                        : dsb.appendRow(new IntSequenceSeries(1, dataFrame.height() + 1));

                for (SeriesBuilder<?> sb : series) {
                    if (xAxisIndex(sb.seriesOpts) == i) {
                        sb.xDimension(pos);
                    }
                }
            }
        }
    }

    // updates both "dsb" (new dataset rows) and some "series" (index of pie labels)
    private void appendPieChartLabels(DatasetBuilder dsb, List<SeriesBuilder<?>> series) {

        for (SeriesBuilder<?> sb : series) {

            if (sb.seriesOpts instanceof PieSeriesOpts) {
                PieSeriesOpts pco = (PieSeriesOpts) sb.seriesOpts;
                int pos = pco.getLabelColumn() != null
                        // TODO: appending pie label data as a Series to prevent column reuse which is not possible until
                        //   https://github.com/apache/echarts/issues/20330 is fixed
                        ? dsb.appendRow(dataFrame.getColumn(pco.getLabelColumn()))
                        : dsb.appendRow(new IntSequenceSeries(1, dataFrame.height() + 1));

                sb.pieLabelsDimension(pos);
            }
        }
    }

    // updates both "dsb" (new dataset rows) and "series" (indices of dataset rows)
    private void appendDatasetRows(DatasetBuilder dsb, List<SeriesBuilder<?>> series) {
        for (SeriesBuilder<?> sb : series) {

            if (sb.dataColumns != null) {

                List<Integer> ys = new ArrayList<>(sb.dataColumns.size());
                sb.dataColumns.forEach(dc -> ys.add(dsb.appendRow(dc)));
                sb.yDimensions(ys);

                // we are laying out DataFrame series as horizontal rows that are somewhat more readable when
                // laid out in JS
                sb.datasetSeriesLayoutBy("row");
            }
        }
    }

    private int xAxisIndex(SeriesOpts<?> series) {
        if (!series.getCoordinateSystemType().isCartesian()) {
            return -1;
        }

        Integer i = null;
        if (series instanceof CartesianSeriesOpts) {
            i = ((CartesianSeriesOpts) series).xAxisIndex;
        }

        // by default should pick the first X axis
        return i != null ? i : 0;
    }
}
