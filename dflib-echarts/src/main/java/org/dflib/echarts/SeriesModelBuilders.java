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

    public static List<SeriesModel> of(
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
        List<SeriesModelBuilder> seriesModels = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {

            SeriesOpts<?> so = opt.seriesOpts.get(i);
            Index dataColumns = opt.seriesDataColumns.get(i);
            String name = nameDeduplicator.apply(defaultName(so, dataColumns));

            seriesModels.add(new SeriesModelBuilder(name, opt, dataFrame, i));
        }

        if (dsb != null) {
            dsb.rows.forEach(r -> linkSeriesToDatasetRow(seriesModels, r, dsb.layoutType));
        }

        return seriesModels.isEmpty()
                ? null
                : seriesModels.stream().map(SeriesModelBuilder::resolve).collect(Collectors.toList());
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

    private static void linkSeriesToDatasetRow(
            List<SeriesModelBuilder> seriesModels,
            DatasetBuilder.DatasetRow row,
            DatasetBuilder.DatasetLayoutType layoutType) {

        switch (row.type) {
            case seriesData -> {
                seriesModels.get(row.seriesOptsPos).datasetSeriesLayoutBy(layoutType.name());

                // multiple dimensions can be appended to the same series in a loop
                seriesModels.get(row.seriesOptsPos).valueDimension(row.datasetPos);
            }
            case xAxisLabels -> {
                for (SeriesModelBuilder sb : seriesModels) {
                    if (xAxisIndex(sb.seriesOpts()) == row.seriesOptsPos) {
                        sb.xDimension(row.datasetPos);
                    }
                }
            }
            case lat -> seriesModels.get(row.seriesOptsPos).latDimension(row.datasetPos);
            case lon -> seriesModels.get(row.seriesOptsPos).lonDimension(row.datasetPos);
            case singleAxisLabel -> {
                for (SeriesModelBuilder sb : seriesModels) {
                    if (singleAxisIndex(sb.seriesOpts()) == row.seriesOptsPos) {
                        sb.singleAxisDimension(row.datasetPos);
                    }
                }
            }
            case symbolSize -> seriesModels.get(row.seriesOptsPos).symbolSizeDimension(row.datasetPos);
            case itemStyleColor -> seriesModels.get(row.seriesOptsPos).itemStyleColorDimension(row.datasetPos);
            case itemName -> seriesModels.get(row.seriesOptsPos).itemNameDimension(row.datasetPos);
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
        if (series instanceof SeriesOptsCoordsSingleAxis s) {
            i = s.getSingleAxisIndex();
        }

        // by default should pick the first axis
        return i != null ? i : 0;
    }
}
