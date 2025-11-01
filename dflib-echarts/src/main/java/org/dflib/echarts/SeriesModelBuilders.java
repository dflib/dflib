package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.echarts.render.option.SeriesModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class SeriesModelBuilders {

    public static SeriesModelBuilders of(
            Option opt,
            DataFrame dataFrame,
            DatasetBuilder dsb) {

        // a stateful function. Can only be used inside the method scope
        UnaryOperator<String> nameDeduplicator = new UnaryOperator<>() {
            final Set<String> names = new HashSet<>();
            int counter;

            @Override
            public String apply(String startName) {
                String name = startName;

                // change in-place... SeriesModelBuilder is not externally visible
                while (!names.add(name)) {
                    name = startName + "_" + counter++;
                }

                return name;
            }
        };

        int len = opt.seriesOpts.size();
        List<SeriesModelBuilder> series = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {

            SeriesOpts<?> so = opt.seriesOpts.get(i);
            Index dataColumns = opt.seriesDataColumns.get(i);
            String name = nameDeduplicator.apply(defaultName(so, dataColumns));

            series.add(new SeriesModelBuilder(name, opt, dataFrame, i));
        }

        if (dsb != null) {
            linkSeriesToRows(series, dsb.rows);
        }

        return new SeriesModelBuilders(series);
    }

    private static String defaultName(SeriesOpts<?> opts, Index dataColumns) {

        if (opts.name != null) {
            return opts.name;
        }

        int len = dataColumns != null ? dataColumns.size() : 0;
        if (len == 1) {
            return dataColumns.get(0);
        } else {
            // either "0" or ">1"
            return opts.getType().name();
        }
    }

    private static void linkSeriesToRows(List<SeriesModelBuilder> series, List<DatasetBuilder.DatasetRow> rows) {
        int len = rows.size();
        for (int i = 0; i < len; i++) {

            DatasetBuilder.DatasetRow row = rows.get(i);
            switch (row.type) {
                case seriesData:

                    // we are laying out DataFrame series as horizontal rows that are somewhat more readable when
                    // laid out in JS
                    series.get(row.pos).datasetSeriesLayoutBy("row");

                    // multiple dimensions can be appended to the same series in a loop
                    series.get(row.pos).valueDimension(i);

                    break;
                case xAxisLabels:
                    for (SeriesModelBuilder sb : series) {
                        if (xAxisIndex(sb.seriesOpts()) == row.pos) {
                            sb.xDimension(i);
                        }
                    }
                    break;
                case singleAxisLabel:
                    for (SeriesModelBuilder sb : series) {
                        if (singleAxisIndex(sb.seriesOpts()) == row.pos) {
                            sb.singleAxisDimension(i);
                        }
                    }
                    break;
                case symbolSize:
                    series.get(row.pos).symbolSizeDimension(i);
                    break;
                case itemStyleColor:
                    series.get(row.pos).itemStyleColorDimension(i);
                    break;
                case itemName:
                    series.get(row.pos).itemNameDimension(i);
                    break;
            }
        }
    }

    private static int xAxisIndex(SeriesOpts<?> series) {
        if (!series.getCoordinateSystemType().isCartesian()) {
            return -1;
        }

        Integer i = null;
        if (series instanceof SeriesOptsCoordsCartesian2D) {
            i = ((SeriesOptsCoordsCartesian2D) series).getXAxisIndex();
        }

        // by default should pick the first X axis
        return i != null ? i : 0;
    }

    private static int singleAxisIndex(SeriesOpts<?> series) {
        if (!series.getCoordinateSystemType().isSingleAxis()) {
            return -1;
        }

        Integer i = null;
        if (series instanceof SeriesOptsCoordsSingleAxis) {
            i = ((SeriesOptsCoordsSingleAxis) series).getSingleAxisIndex();
        }

        // by default should pick the first axis
        return i != null ? i : 0;
    }

    private final List<SeriesModelBuilder> builders;

    private SeriesModelBuilders(List<SeriesModelBuilder> builders) {
        this.builders = builders;
    }

    public List<SeriesModel> resolve() {
        return builders.isEmpty()
                ? null
                : builders.stream().map(SeriesModelBuilder::resolve).collect(Collectors.toList());
    }
}
