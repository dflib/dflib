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

class OpToOptionModel {

    private final Option opt;
    private final DataFrame dataFrame;

    OpToOptionModel(Option opt, DataFrame dataFrame) {
        this.opt = Objects.requireNonNull(opt);
        this.dataFrame = Objects.requireNonNull(dataFrame);
    }

    OptionModel resolve() {

        List<OptionSeriesBuilder<?>> series = dedupeSeriesNames();
        boolean cartesianDefaults = useCartesianDefaults(series);

        List<OptionXAxisBuilder> xs = opt.xAxes != null
                ? opt.xAxes
                : (cartesianDefaults ? List.of(new OptionXAxisBuilder(null, XAxis.ofDefault())) : null);
        List<YAxis> ys = opt.yAxes != null ? opt.yAxes : (cartesianDefaults ? List.of(YAxis.ofDefault()) : null);

        OptionDatasetBuilder dsb = new OptionDatasetBuilder(dataFrame);
        appendXAxesLabels(dsb, xs);
        appendPieChartLabels(dsb, series);
        appendDatasetRows(dsb, series);

        return new OptionModel(
                dsb.datasetModel(),
                opt.legend != null ? opt.legend : false,
                grids(),
                seriesModels(series),
                opt.title,
                opt.toolbox != null ? opt.toolbox.resolve() : null,
                opt.tooltip != null ? opt.tooltip.resolve() : null,
                xs != null ? xs.stream().map(OptionXAxisBuilder::getAxis).map(XAxis::resolve).collect(Collectors.toList()) : null,
                ys != null ? ys.stream().map(YAxis::resolve).collect(Collectors.toList()) : null
        );
    }

    private List<OptionSeriesBuilder<?>> dedupeSeriesNames() {

        if (opt.series.size() < 2) {
            return opt.series;
        }

        UnaryOperator<OptionSeriesBuilder<?>> nameDeduplicator = new UnaryOperator<>() {
            final Set<String> names = new HashSet<>();
            int counter;

            @Override
            public OptionSeriesBuilder<?> apply(OptionSeriesBuilder<?> sb) {
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

    List<SeriesModel> seriesModels(List<OptionSeriesBuilder<?>> series) {
        if (series.isEmpty()) {
            return null;
        }

        OpToSeriesModel resolver = new OpToSeriesModel();
        return series.stream().map(resolver::resolve).collect(Collectors.toList());
    }

    private boolean useCartesianDefaults(List<OptionSeriesBuilder<?>> series) {
        return series.isEmpty()
                || series.stream().anyMatch(sb -> sb.opts.getType().isCartesian());
    }

    // updates both "dsb" (new dataset rows) and "xs" (label indices)
    private void appendXAxesLabels(OptionDatasetBuilder dsb, List<OptionXAxisBuilder> xs) {

        if (xs != null) {
            int len = xs.size();
            for (int i = 0; i < len; i++) {
                OptionXAxisBuilder ab = xs.get(i);
                int pos = ab.columnName != null
                        ? dsb.appendRow(ab.columnName)
                        : dsb.appendRow(new IntSequenceSeries(1, dataFrame.height() + 1));
                ab.datasetRowIndex(pos);
            }
        }
    }

    // updates both "dsb" (new dataset rows) and some "series" (index of pie labels)
    private void appendPieChartLabels(OptionDatasetBuilder dsb, List<OptionSeriesBuilder<?>> series) {

        for (OptionSeriesBuilder<?> sb : series) {

            if (sb.opts instanceof PieSeriesOpts) {
                PieSeriesOpts pco = (PieSeriesOpts) sb.opts;
                int pos = pco.getLabelColumn() != null
                        ? dsb.appendRow(pco.getLabelColumn())
                        : dsb.appendRow(new IntSequenceSeries(1, dataFrame.height() + 1));

                sb.pieLabelsDimension(pos);
            }
        }
    }

    // updates both "dsb" (new dataset rows) and "series" (indices of dataset rows)
    private void appendDatasetRows(OptionDatasetBuilder dsb, List<OptionSeriesBuilder<?>> series) {
        for (OptionSeriesBuilder<?> sb : series) {

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
}
