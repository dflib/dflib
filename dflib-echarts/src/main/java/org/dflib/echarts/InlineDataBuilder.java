package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.render.ValueModels;
import org.dflib.echarts.render.option.data.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Builds the "data" section embedded in the SeriesModel. The opposite of DatasetBuilder that creates a reusable
 * "dataset" outside SeriesModel. Unlike row-based datasets, the {@link DataModel} generated here is column-based.
 */
class InlineDataBuilder {

    public static InlineDataBuilder of(DataFrame dataFrame) {
        return new InlineDataBuilder(dataFrame);
    }

    final DataFrame dataFrame;
    final List<Series<?>> cols;

    private final Map<String, Integer> colPosByDataColumn;
    private final Map<Integer, String> dataColumnByColPos;

    private InlineDataBuilder(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
        this.cols = new ArrayList<>();
        this.colPosByDataColumn = new HashMap<>();
        this.dataColumnByColPos = new HashMap<>();
    }

    public int appendCol(Series<?> column) {
        int pos = cols.size();
        cols.add(Objects.requireNonNull(column));
        return pos;
    }

    public int appendCol(String dataColumnName) {
        Objects.requireNonNull(dataColumnName);

        Integer existingPos = colPosByDataColumn.get(dataColumnName);
        if (existingPos != null) {
            return existingPos;
        }

        int pos = cols.size();
        cols.add(dataFrame.getColumn(dataColumnName));
        colPosByDataColumn.put(dataColumnName, pos);
        dataColumnByColPos.put(pos, dataColumnName);
        return pos;
    }

    public DataModel dataModel() {
        if (cols.isEmpty()) {
            return null;
        }

        int h = dataFrame.height();
        int w = cols.size();

        Supplier<String> labelMaker = new Supplier<>() {

            String baseLabel = "L";
            int labelCounter = 0;
            Set<String> seen = new HashSet<>(colPosByDataColumn.keySet());

            @Override
            public String get() {
                String label;
                do {
                    label = baseLabel + labelCounter++;
                } while (!seen.add(label));

                return label;
            }
        };

        List<ValueModels<?>> rows = new ArrayList<>(h + 1);

        // append column labels
        List<Object> labels = new ArrayList<>(w);
        for (int j = 0; j < w; j++) {
            String dataColumn = dataColumnByColPos.get(j);
            String rowLabel = dataColumn != null ? dataColumn : labelMaker.get();
            labels.add(rowLabel);
        }

        rows.add(new ValueModels(labels));

        // append data
        for (int i = 0; i < h; i++) {

            List<Object> row = new ArrayList<>(w);

            for (int j = 0; j < w; j++) {
                row.add(cols.get(j).get(i));
            }

            rows.add(new ValueModels(row));
        }

        return new DataModel(new ValueModels(rows));
    }
}
