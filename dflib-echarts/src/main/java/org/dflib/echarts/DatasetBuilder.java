package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.echarts.render.ValueModels;
import org.dflib.echarts.render.option.dataset.DatasetModel;
import org.dflib.series.IntSequenceSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

class DatasetBuilder {

    public static DatasetBuilder of(Option opt, DataFrame dataFrame) {

        // The default for no series is to show empty cartesian coordinates, so "dataset" is still needed
        boolean needsDataset = opt.seriesOpts.isEmpty() || opt.seriesOpts.stream()
                .filter(so -> so.getType().supportsDataset())
                .map(so -> true)
                .findFirst()
                .orElse(false);

        return needsDataset ? create(opt, dataFrame) : null;
    }

    private static DatasetBuilder create(Option opt, DataFrame dataFrame) {
        return new DatasetBuilder(dataFrame)
                .appendXAxesLabels(opt.xAxes)
                .appendSingleAxesLabels(opt.singleAxes)
                .appendSymbolSizes(opt.seriesOpts)
                .appendItemStyleColors(opt.seriesOpts)
                .appendItemNames(opt.seriesOpts)
                .appendDatasetRows(opt.seriesDataColumns);
    }

    private final DataFrame dataFrame;
    final List<DatasetRow> rows;
    private final Set<String> seenDataColumns;
    private final Map<Integer, String> dataColumnByRowPos;

    DatasetBuilder(DataFrame dataFrame) {
        this.dataFrame = dataFrame;

        this.rows = new ArrayList<>();
        this.seenDataColumns = new HashSet<>();
        this.dataColumnByRowPos = new HashMap<>();
    }

    private DatasetBuilder appendXAxesLabels(List<ColumnLinkedXAxis> xs) {

        if (xs != null) {
            int len = xs.size();
            for (int i = 0; i < len; i++) {
                ColumnLinkedXAxis ab = xs.get(i);
                if (ab.columnName != null) {
                    appendExtraRow(dataFrame.getColumn(ab.columnName), DatasetRowType.xAxisLabels, i);
                } else {
                    appendExtraRow(new IntSequenceSeries(1, dataFrame.height() + 1), DatasetRowType.xAxisLabels, i);
                }
            }
        }

        return this;
    }

    private DatasetBuilder appendSingleAxesLabels(List<ColumnLinkedSingleAxis> ss) {

        if (ss != null) {
            int len = ss.size();
            for (int i = 0; i < len; i++) {
                ColumnLinkedSingleAxis ab = ss.get(i);
                if (ab.columnName != null) {
                    appendExtraRow(dataFrame.getColumn(ab.columnName), DatasetRowType.singleAxisLabel, i);
                }
            }
        }

        return this;
    }

    private DatasetBuilder appendSymbolSizes(List<SeriesOpts<?>> series) {

        int len = series.size();
        for (int i = 0; i < len; i++) {

            SeriesOpts<?> so = series.get(i);

            if (so instanceof SeriesOptsItemSymbolSize) {
                SeriesOptsItemSymbolSize soc = (SeriesOptsItemSymbolSize) so;
                String columnName = soc.getItemSymbolSizeSeries();
                if (columnName != null) {
                    appendExtraRow(dataFrame.getColumn(columnName), DatasetRowType.symbolSize, i);
                }
            }
        }

        return this;
    }

    private DatasetBuilder appendItemStyleColors(List<SeriesOpts<?>> series) {
        int len = series.size();
        for (int i = 0; i < len; i++) {

            SeriesOpts<?> so = series.get(i);

            if (so instanceof SeriesOptsItemStyleColor) {
                SeriesOptsItemStyleColor soc = (SeriesOptsItemStyleColor) so;
                String columnName = soc.getItemStyleColorSeries();
                if (columnName != null) {
                    appendExtraRow(dataFrame.getColumn(columnName), DatasetRowType.itemStyleColor, i);
                }
            }
        }

        return this;
    }

    private DatasetBuilder appendItemNames(List<SeriesOpts<?>> series) {

        int len = series.size();
        for (int i = 0; i < len; i++) {

            SeriesOpts<?> so = series.get(i);

            if (so instanceof SeriesOptsNamedItems) {
                SeriesOptsNamedItems soc = (SeriesOptsNamedItems) so;
                if (soc.getItemNameSeries() != null) {
                    appendExtraRow(dataFrame.getColumn(soc.getItemNameSeries()), DatasetRowType.itemName, i);
                }
            }
        }

        return this;
    }

    private DatasetBuilder appendDatasetRows(List<Index> seriesDataColumns) {
        int len = seriesDataColumns.size();
        for (int i = 0; i < len; i++) {

            Index dataColumns = seriesDataColumns.get(i);
            if (dataColumns != null) {
                for (String dc : dataColumns) {
                    appendChartSeriesRow(dc, i);
                }
            }
        }

        return this;
    }

    private void appendChartSeriesRow(String dataColumnName, int seriesPos) {
        Objects.requireNonNull(dataColumnName);

        // "dataColumnName" can be used multiple times (e.g., when the same column is used by multiple plot series).
        // We'll register a separate dataset row for each one of the duplicates.

        int pos = rows.size();
        rows.add(new DatasetRow(dataFrame.getColumn(dataColumnName), DatasetRowType.seriesData, seriesPos));
        seenDataColumns.add(dataColumnName);
        dataColumnByRowPos.put(pos, dataColumnName);

    }

    // Append a row that is not present in the DataFrame.
    // Such rows may get duplicated, as there's no key that we can use to cache it
    private void appendExtraRow(Series<?> row, DatasetRowType type, int seriesPos) {
        rows.add(new DatasetRow(Objects.requireNonNull(row), type, seriesPos));
    }

    public DatasetModel resolve() {

        if (rows.isEmpty()) {
            return null;
        }

        // DF columns become rows and rows become columns in the EChart dataset
        int w = dataFrame.height();
        int h = rows.size();

        Supplier<String> labelMaker = new Supplier<>() {

            final String baseLabel = "L";
            final Set<String> seen = new HashSet<>(seenDataColumns);
            int labelCounter = 0;

            @Override
            public String get() {
                String label;
                do {
                    label = baseLabel + labelCounter++;
                } while (!seen.add(label));

                return label;
            }
        };

        List<ValueModels<?>> rows = new ArrayList<>(h);
        for (int i = 0; i < h; i++) {

            List<Object> row = new ArrayList<>(w + 1);

            String dataColumn = dataColumnByRowPos.get(i);
            String rowLabel = dataColumn != null ? dataColumn : labelMaker.get();

            row.add(rowLabel);
            Series<?> rowData = this.rows.get(i).data;

            for (int j = 0; j < w; j++) {
                row.add(rowData.get(j));
            }

            rows.add(ValueModels.of(row));
        }

        return new DatasetModel(ValueModels.of(rows));
    }

    enum DatasetRowType {
        xAxisLabels, singleAxisLabel, symbolSize, itemStyleColor, itemName, seriesData
    }

    static class DatasetRow {
        final Series<?> data;
        final DatasetRowType type;
        final int seriesOptsPos;

        DatasetRow(Series<?> data, DatasetRowType type, int seriesOptsPos) {
            this.type = type;
            this.data = data;
            this.seriesOptsPos = seriesOptsPos;
        }
    }
}
