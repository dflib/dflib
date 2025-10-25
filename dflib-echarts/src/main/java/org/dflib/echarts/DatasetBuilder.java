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

    public static DatasetBuilder of(
            Option opt, DataFrame dataFrame) {

        // The default for no series is to show empty cartesian coordinates, so "dataset" is still needed
        boolean needsDataset = opt.seriesOpts.isEmpty() || opt.seriesOpts.stream()
                .filter(so -> so.getType().supportsDataset())
                .map(so -> true)
                .findFirst()
                .orElse(false);

        if (!needsDataset) {
            return null;
        }

        DatasetBuilder dsb = new DatasetBuilder(dataFrame);
        appendXAxesLabels(dsb, opt.xAxes);
        appendSymbolSizes(dsb, opt.seriesOpts);
        appendPieChartLabels(dsb, opt.seriesOpts);
        appendDatasetRows(dsb, opt.seriesDataColumns);

        return dsb;
    }

    private static void appendXAxesLabels(DatasetBuilder dsb, List<XAxisBuilder> xs) {

        if (xs != null) {
            int len = xs.size();
            for (int i = 0; i < len; i++) {
                XAxisBuilder ab = xs.get(i);
                if (ab.columnName != null) {
                    dsb.appendRow(dsb.dataFrame.getColumn(ab.columnName), DatasetRowType.xAxisLabels, i);
                } else {
                    // TODO: appending X data as a Series to prevent column reuse which is not possible until
                    //  https://github.com/apache/echarts/issues/20330 is fixed
                    dsb.appendRow(new IntSequenceSeries(1, dsb.dataFrame.height() + 1), DatasetRowType.xAxisLabels, i);
                }
            }
        }
    }

    private static void appendSymbolSizes(DatasetBuilder dsb, List<SeriesOpts<?>> series) {

        int len = series.size();
        for (int i = 0; i < len; i++) {

            SeriesOpts<?> opts = series.get(i);

            if (opts instanceof LineSeriesOpts) {
                LineSeriesOpts sso = (LineSeriesOpts) opts;
                if (sso.symbolSize != null && sso.symbolSize.column != null) {
                    // TODO: appending X data as a Series to prevent column reuse. Two problems with reuse:
                    //  1. https://github.com/apache/echarts/issues/20330
                    //  2. The row type may change, and we won't detect that it is a label (this is fixable in DFLib by allowing multiple row types)
                    dsb.appendRow(dsb.dataFrame.getColumn(sso.symbolSize.column), DatasetRowType.symbolSize, i);
                }
            } else if (opts instanceof ScatterSeriesOpts) {
                ScatterSeriesOpts sso = (ScatterSeriesOpts) opts;
                if (sso.symbolSize != null && sso.symbolSize.column != null) {
                    // TODO: appending X data as a Series to prevent column reuse. Two problems with reuse:
                    //  1. https://github.com/apache/echarts/issues/20330
                    //  2. The row type may change, and we won't detect that it is a label (this is fixable in DFLib by allowing multiple row types)
                    dsb.appendRow(dsb.dataFrame.getColumn(sso.symbolSize.column), DatasetRowType.symbolSize, i);
                }
            }
        }
    }

    private static void appendPieChartLabels(DatasetBuilder dsb, List<SeriesOpts<?>> series) {

        int len = series.size();
        for (int i = 0; i < len; i++) {

            SeriesOpts<?> opts = series.get(i);

            if (opts instanceof PieSeriesOpts) {
                PieSeriesOpts pco = (PieSeriesOpts) opts;
                if (pco.getLabelColumn() != null) {
                    dsb.appendRow(dsb.dataFrame.getColumn(pco.getLabelColumn()), DatasetRowType.pieItemName, i);
                } else {
                    // TODO: appending pie label data as a Series to prevent column reuse which is not possible until
                    //   https://github.com/apache/echarts/issues/20330 is fixed
                    dsb.appendRow(new IntSequenceSeries(1, dsb.dataFrame.height() + 1), DatasetRowType.pieItemName, i);
                }
            }
        }
    }

    private static void appendDatasetRows(DatasetBuilder dsb, List<Index> seriesDataColumns) {
        int len = seriesDataColumns.size();
        for (int i = 0; i < len; i++) {

            Index dataColumns = seriesDataColumns.get(i);
            if (dataColumns != null) {
                for (String dc : dataColumns) {
                    dsb.appendRow(dc, DatasetRowType.seriesData, i);
                }
            }
        }
    }


    private final DataFrame dataFrame;

    final List<DatasetRow> rows;
    private final Map<String, Integer> rowPosByDataColumn;
    private final Map<Integer, String> dataColumnByRowPos;

    DatasetBuilder(DataFrame dataFrame) {
        this.dataFrame = dataFrame;

        this.rows = new ArrayList<>();
        this.rowPosByDataColumn = new HashMap<>();
        this.dataColumnByRowPos = new HashMap<>();
    }

    // Append a row that is not present in the DataFrame.
    // Such rows may get duplicated, as there's no key that we can use to cache it
    private int appendRow(Series<?> row, DatasetRowType type, int seriesPos) {
        int pos = rows.size();
        rows.add(new DatasetRow(Objects.requireNonNull(row), type, seriesPos));
        return pos;
    }

    private int appendRow(String dataColumnName, DatasetRowType type, int seriesPos) {
        Objects.requireNonNull(dataColumnName);

        Integer existingPos = rowPosByDataColumn.get(dataColumnName);
        if (existingPos != null) {
            return existingPos;
        }

        int pos = rows.size();
        rows.add(new DatasetRow(dataFrame.getColumn(dataColumnName), type, seriesPos));
        rowPosByDataColumn.put(dataColumnName, pos);
        dataColumnByRowPos.put(pos, dataColumnName);
        return pos;
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
            final Set<String> seen = new HashSet<>(rowPosByDataColumn.keySet());
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
        xAxisLabels, symbolSize, pieItemName, seriesData
    }

    static class DatasetRow {
        final Series<?> data;
        final DatasetRowType type;
        final int pos;

        DatasetRow(Series<?> data, DatasetRowType type, int pos) {
            this.type = type;
            this.data = data;
            this.pos = pos;
        }
    }
}
