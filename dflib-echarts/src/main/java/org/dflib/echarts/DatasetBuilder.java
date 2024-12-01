package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.render.ValueModels;
import org.dflib.echarts.render.option.dataset.DatasetModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

class DatasetBuilder {

    private final DataFrame dataFrame;

    private final List<DatasetRow> rows;
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
    int append(Series<?> row, DatasetRowType type, int seriesPos) {
        int pos = rows.size();
        rows.add(new DatasetRow(Objects.requireNonNull(row), type, seriesPos));
        return pos;
    }

    int append(String dataColumnName, DatasetRowType type, int seriesPos) {
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

    void linkSeriesToRows(List<SeriesBuilder<?>> series) {
        int len = rows.size();
        for (int i = 0; i < len; i++) {

            DatasetRow row = rows.get(i);
            switch (row.type) {
                case seriesData:

                    // we are laying out DataFrame series as horizontal rows that are somewhat more readable when
                    // laid out in JS
                    series.get(row.seriesIndex).datasetSeriesLayoutBy("row");

                    // multiple dimensions can be appended to the same series in a loops
                    series.get(row.seriesIndex).yDimension(i);

                    break;
                case xAxisLabels:
                    for (SeriesBuilder<?> sb : series) {
                        if (xAxisIndex(sb.seriesOpts) == row.xAxisIndex) {
                            sb.xDimension(i);
                        }
                    }
                    break;
                case pieItemName:
                    series.get(row.pieSeriesIndex).pieLabelsDimension(i);
                    break;
            }
        }
    }

    DatasetModel datasetModel() {

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
