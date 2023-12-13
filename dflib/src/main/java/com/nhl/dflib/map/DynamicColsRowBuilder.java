package com.nhl.dflib.map;

import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.RowBuilder;
import com.nhl.dflib.series.ArraySeries;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.0-M19
 */
public class DynamicColsRowBuilder implements RowBuilder {

    private final Map<String, Object[]> columns;
    private final int height;

    private int row;

    public DynamicColsRowBuilder(int height) {

        // using Object[] instead of ValueAccum as this allows to rewind rows without the need to initialize previous
        // row with nulls ... The tradeoff is the inability to use primitive accums.
        this.columns = new LinkedHashMap<>();
        this.height = height;
    }

    public String[] getLabels() {
        return columns.keySet().toArray(new String[0]);
    }

    public Series<?>[] getData() {
        int w = columns.size();
        Series[] series = new Series[w];

        Iterator<Object[]> it = columns.values().iterator();

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(it.next());
        }

        return series;
    }

    @Override
    public Index getIndex() {
        // index is only available *after* the iteration is complete
        throw new UnsupportedOperationException("Access to index is not supported by DynamicColsRowBuilder");
    }

    @Override
    public void set(String columnName, Object value) {
        columns.computeIfAbsent(columnName, n -> new Object[height])[row] = value;
    }

    @Override
    public void set(int columnPos, Object value) {
        throw new UnsupportedOperationException("'set' by position is not supported by DynamicColsRowBuilder. Use 'set(String..)' instead");
    }

    @Override
    public void setRange(Object[] values, int fromOffset, int toOffset, int len) {
        throw new UnsupportedOperationException("'setRange' is not supported by DynamicColsRowBuilder. Use 'set(String..)' instead");
    }

    public void reset() {
        row++;
    }
}
