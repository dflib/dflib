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
            DataFrame dataFrame,
            Option opt,
            List<XAxisBuilder> xs) {

        DatasetBuilder dsb = new DatasetBuilder(dataFrame);
        appendXAxesLabels(dsb, xs);
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
                    dsb.append(dsb.dataFrame.getColumn(ab.columnName), DatasetBuilder.DatasetRowType.xAxisLabels, i);
                } else {
                    // TODO: appending X data as a Series to prevent column reuse which is not possible until
                    //  https://github.com/apache/echarts/issues/20330 is fixed
                    dsb.append(new IntSequenceSeries(1, dsb.dataFrame.height() + 1), DatasetBuilder.DatasetRowType.xAxisLabels, i);
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
                    dsb.append(dsb.dataFrame.getColumn(pco.getLabelColumn()), DatasetBuilder.DatasetRowType.pieItemName, i);
                } else {
                    // TODO: appending pie label data as a Series to prevent column reuse which is not possible until
                    //   https://github.com/apache/echarts/issues/20330 is fixed
                    dsb.append(new IntSequenceSeries(1, dsb.dataFrame.height() + 1), DatasetBuilder.DatasetRowType.pieItemName, i);
                }
            }
        }
    }

    // updates both "dsb" (new dataset rows) and "series" (indices of dataset rows)
    private static void appendDatasetRows(DatasetBuilder dsb, List<Index> seriesDataColumns) {
        int len = seriesDataColumns.size();
        for (int i = 0; i < len; i++) {

            Index dataColumns = seriesDataColumns.get(i);
            if (dataColumns != null) {
                for (String dc : dataColumns) {
                    dsb.append(dc, DatasetBuilder.DatasetRowType.seriesData, i);
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
    private int append(Series<?> row, DatasetRowType type, int seriesPos) {
        int pos = rows.size();
        rows.add(new DatasetRow(Objects.requireNonNull(row), type, seriesPos));
        return pos;
    }

    private int append(String dataColumnName, DatasetRowType type, int seriesPos) {
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

    public DatasetModel datasetModel() {

        if (rows.isEmpty()) {
            return null;
        }

        // DF columns become rows and rows become columns in the EChart dataset
        int w = dataFrame.height();
        int h = rows.size();

        Supplier<String> labelMaker = new Supplier<>() {

            String baseLabel = "L";
            int labelCounter = 0;
            Set<String> seen = new HashSet<>(rowPosByDataColumn.keySet());

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
        xAxisLabels, pieItemName, seriesData
    }

    static class DatasetRow {
        final Series<?> data;
        final DatasetRowType type;
        final int xAxisIndex;
        final int pieSeriesIndex;
        final int seriesIndex;

        DatasetRow(Series<?> data, DatasetRowType type, int pos) {

            this.type = type;
            this.data = data;

            switch (type) {
                case pieItemName:
                    this.pieSeriesIndex = pos;
                    this.xAxisIndex = -1;
                    this.seriesIndex = -1;
                    break;
                case seriesData:
                    this.pieSeriesIndex = -1;
                    this.xAxisIndex = -1;
                    this.seriesIndex = pos;
                    break;
                case xAxisLabels:
                    this.pieSeriesIndex = -1;
                    this.xAxisIndex = pos;
                    this.seriesIndex = -1;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported dataset row type: " + type);
            }
        }
    }
}
