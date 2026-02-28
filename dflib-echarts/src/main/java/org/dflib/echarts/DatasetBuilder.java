package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.echarts.render.ValueModels;
import org.dflib.echarts.render.option.dataset.DatasetModel;
import org.dflib.series.IntSequenceSeries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

class DatasetBuilder {

    public static DatasetBuilder of(Option opt, DataFrame dataFrame) {

        // The default for no series is to show empty Cartesian coordinates that presumably requires a dataset
        // (see Option.resolveDefaults())
        // TODO: geo-related check is a hack. How do we make it more generic?

        boolean needsDataset = (opt.seriesOpts.isEmpty() && opt.geo == null)
                || opt.seriesOpts.stream()
                .filter(so -> so.getType().supportsDataset())
                .map(so -> true)
                .findFirst()
                .orElse(false);

        return needsDataset ? create(opt, dataFrame) : null;
    }

    private static DatasetBuilder create(Option opt, DataFrame dataFrame) {
        return new DatasetBuilder(dataFrame, datasetLayoutType(opt.seriesOpts))
                .appendXAxesLabels(opt.xAxes)
                .appendSingleAxesLabels(opt.singleAxes)
                .appendGeoCoordinates(opt.seriesOpts)
                .appendSymbolSizes(opt.seriesOpts)
                .appendItemStyleColors(opt.seriesOpts)
                .appendItemNames(opt.seriesOpts)
                .appendDatasetRows(opt.seriesDataColumns);
    }

    private final DataFrame dataFrame;
    final DatasetLayoutType layoutType;
    final List<DatasetRow> rows;
    private final Set<String> seenDataColumns;

    private DatasetBuilder(DataFrame dataFrame, DatasetLayoutType layoutType) {
        this.dataFrame = dataFrame;
        this.layoutType = layoutType;

        this.rows = new ArrayList<>();
        this.seenDataColumns = new HashSet<>();
    }

    private static DatasetLayoutType datasetLayoutType(List<SeriesOpts<?>> seriesOpts) {
        // "row" is the preferred layout type, as more compact and readable. "column" is used in exceptional cases
        // specifically, to address this bug until ECharts does: https://github.com/apache/echarts/issues/21520

        return seriesOpts
                .stream()
                .filter(s -> s.getType() == ChartType.map)
                .findFirst()
                .map(s -> DatasetLayoutType.column)
                .orElse(DatasetLayoutType.row);
    }

    private DatasetBuilder appendXAxesLabels(List<ColumnLinkedXAxis> xs) {

        if (xs != null) {
            int len = xs.size();
            for (int i = 0; i < len; i++) {
                ColumnLinkedXAxis ab = xs.get(i);
                if (ab.columnName != null) {
                    appendUnnamedRow(dataFrame.getColumn(ab.columnName), DatasetRowType.xAxisLabels, i);
                } else {
                    appendUnnamedRow(new IntSequenceSeries(1, dataFrame.height() + 1), DatasetRowType.xAxisLabels, i);
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
                    appendUnnamedRow(dataFrame.getColumn(ab.columnName), DatasetRowType.singleAxisLabel, i);
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
                    appendUnnamedRow(dataFrame.getColumn(columnName), DatasetRowType.symbolSize, i);
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
                    appendUnnamedRow(dataFrame.getColumn(columnName), DatasetRowType.itemStyleColor, i);
                }
            }
        }

        return this;
    }

    private DatasetBuilder appendGeoCoordinates(List<SeriesOpts<?>> series) {

        int len = series.size();
        for (int i = 0; i < len; i++) {

            if (series.get(i) instanceof SeriesOptsLonLat gc) {
                if (gc.getLatSeries() != null) {
                    appendUnnamedRow(dataFrame.getColumn(gc.getLatSeries()), DatasetRowType.lat, i);
                }

                if (gc.getLonSeries() != null) {
                    appendUnnamedRow(dataFrame.getColumn(gc.getLonSeries()), DatasetRowType.lon, i);
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
                String columnName = soc.getItemNameSeries();
                if (columnName != null) {
                    appendUnnamedRow(dataFrame.getColumn(soc.getItemNameSeries()), DatasetRowType.itemName, i);
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

    private void appendChartSeriesRow(String dataColumn, int seriesPos) {
        Objects.requireNonNull(dataColumn);

        // "dataColumn" can be used multiple times (e.g., when the same column is used by multiple plot series).
        // We'll register a separate dataset row for each one of the duplicates.

        int pos = rows.size();
        rows.add(new DatasetRow(dataFrame.getColumn(dataColumn), DatasetRowType.seriesData, dataColumn, seriesPos, pos));
        seenDataColumns.add(dataColumn);
    }

    // Append a row that is not present in the DataFrame.
    // Such rows may get duplicated, as there's no key that we can use to cache it
    private void appendUnnamedRow(Series<?> row, DatasetRowType type, int seriesPos) {
        rows.add(new DatasetRow(Objects.requireNonNull(row), type, null, seriesPos, rows.size()));
    }

    public DatasetModel resolve() {

        if (rows.isEmpty()) {
            return null;
        }

        return switch (layoutType) {
            case column -> resolveColumn();
            case row -> resolveRow();
        };
    }

    private DatasetModel resolveRow() {

        int w = dataFrame.height();
        int h = rows.size();

        Supplier<String> labelMaker = createLabelMaker();

        List<ValueModels<?>> mRows = new ArrayList<>(h);
        for (DatasetRow dsRow : this.rows) {

            List<Object> mRow = new ArrayList<>(w + 1);

            String rowLabel = dsRow.dfColumn != null ? dsRow.dfColumn : labelMaker.get();
            mRow.add(rowLabel);
            dsRow.data.forEach(mRow::add);

            mRows.add(new ValueModels<>(mRow));
        }

        return new DatasetModel(new ValueModels<>(mRows));
    }

    private DatasetModel resolveColumn() {

        int w = dataFrame.height();
        int h = rows.size();

        Supplier<String> labelMaker = createLabelMaker();

        // Resolve labels for all dataset rows (which correspond to ECharts columns)
        List<String> labels = new ArrayList<>(h);
        for (DatasetRow dsRow : this.rows) {
            labels.add(dsRow.dfColumn != null ? dsRow.dfColumn : labelMaker.get());
        }

        List<ValueModels<?>> mRows = new ArrayList<>(w + 1);

        // First row: all column labels
        mRows.add(new ValueModels<>(new ArrayList<>(labels)));

        // Subsequent rows: one per DataFrame row, with one value per dataset column
        for (int i = 0; i < w; i++) {
            List<Object> mRow = new ArrayList<>(h);
            for (DatasetRow dsRow : this.rows) {
                mRow.add(dsRow.data.get(i));
            }
            mRows.add(new ValueModels<>(mRow));
        }

        return new DatasetModel(new ValueModels<>(mRows));
    }

    private Supplier<String> createLabelMaker() {
        return new Supplier<>() {

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
    }

    enum DatasetRowType {
        xAxisLabels, singleAxisLabel, lat, lon, symbolSize, itemStyleColor, itemName, seriesData
    }

    static class DatasetRow {

        final DatasetRowType type;
        final int seriesOptsPos;
        final int datasetPos;

        private final Series<?> data;
        private final String dfColumn;

        DatasetRow(Series<?> data, DatasetRowType type, String dfColumn, int seriesOptsPos, int datasetPos) {
            this.data = Objects.requireNonNull(data);
            this.type = Objects.requireNonNull(type);
            this.dfColumn = dfColumn;
            this.seriesOptsPos = seriesOptsPos;
            this.datasetPos = datasetPos;
        }
    }

    enum DatasetLayoutType {
        row, column
    }
}
