package org.dflib.builder;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 2.0.0
 */
public class DataFrameByMapRowBuilder {

    private Index columnsIndex;
    private int capacity;

    public DataFrameByMapRowBuilder columnNames(String... columnNames) {
        return columnIndex(Index.of(columnNames));
    }

    public DataFrameByMapRowBuilder columnIndex(Index columnsIndex) {
        this.columnsIndex = columnsIndex;
        return this;
    }

    /**
     * Explicitly sets builder "vertical" capacity to avoid internal array resizing. If not set, DFLib will make a
     * guess if possible or use a default.
     */
    public DataFrameByMapRowBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public DataFrame of(Iterable<Map<String, ?>> source) {
        int capacity = guessCapacity(source);
        return columnsIndex != null ? ofFixedIndex(source, columnsIndex, capacity) : ofDynamicIndex(source, capacity);
    }

    private DataFrame ofFixedIndex(Iterable<Map<String, ?>> source, Index columnsIndex, int capacity) {

        String[] names = columnsIndex.toArray();
        int w = names.length;

        ObjectAccum[] accums = new ObjectAccum[w];
        for (int i = 0; i < w; i++) {
            accums[i] = new ObjectAccum<>(capacity);
        }

        for (Map<String, ?> m : source) {
            for (int i = 0; i < w; i++) {
                accums[i].push(m.get(names[i]));
            }
        }

        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = accums[i].toSeries();
        }

        return new ColumnDataFrame(null, columnsIndex, columns);
    }

    private DataFrame ofDynamicIndex(Iterable<Map<String, ?>> maps, int capacity) {

        Map<String, ObjectAccum> accums = new HashMap<>();

        {
            int i = 0;
            for (Map<String, ?> m : maps) {
                for (Map.Entry<String, ?> e : m.entrySet()) {
                    int fi = i;
                    accums.computeIfAbsent(e.getKey(), k -> accumWithOffset(capacity, fi)).push(e.getValue());
                }

                for (Map.Entry<String, ObjectAccum> e : accums.entrySet()) {
                    if (!m.containsKey(e.getKey())) {
                        e.getValue().push(null);
                    }
                }

                i++;
            }
        }

        String[] names = accums.keySet().toArray(new String[0]);
        Arrays.sort(names);

        int w = names.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = accums.get(names[i]).toSeries();
        }

        return new ColumnDataFrame(null, Index.of(names), columns);
    }

    private ObjectAccum<?> accumWithOffset(int capacity, int offset) {
        ObjectAccum<?> accum = new ObjectAccum<>(capacity);
        accum.fill(0, offset, null);
        return accum;
    }

    private int guessCapacity(Iterable<?> source) {
        if (this.capacity > 0) {
            return this.capacity;
        } else {
            return BuilderCapacity.capacity(source);
        }
    }
}
