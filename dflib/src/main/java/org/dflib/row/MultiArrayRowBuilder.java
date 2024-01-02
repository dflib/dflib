package org.dflib.row;

import org.dflib.Index;
import org.dflib.Series;
import org.dflib.series.ArraySeries;

public class MultiArrayRowBuilder implements RowBuilder {

    private final Index index;
    private final Object[][] data;

    private int rowIndex;

    public MultiArrayRowBuilder(Index index, int height) {
        this.index = index;

        // using Object[] instead of ValueAccum as this allows to rewind rows without the need to initialize previous
        // row with nulls ... The tradeoff is the inability to use primitive accums.
        this.data = new Object[index.size()][height];
        this.rowIndex = -1;
    }

    public void next() {
        rowIndex++;
    }

    public Series<?>[] getData() {
        int w = index.size();
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries<>(data[i]);
        }

        return series;
    }

    @Override
    public Index getIndex() {
        return index;
    }

    @Override
    public void set(String columnName, Object value) {
        data[index.position(columnName)][rowIndex] = value;
    }

    @Override
    public void set(int columnPos, Object value) {
        data[columnPos][rowIndex] = value;
    }

    @Override
    public void setRange(Object[] values, int fromOffset, int toOffset, int len) {

        if (len + toOffset > index.size()) {
            throw new IllegalArgumentException("Provided values won't fit in the row: "
                    + (len + toOffset)
                    + " > "
                    + index.size());
        }

        for (int i = 0; i < len; i++) {
            data[toOffset + i][rowIndex] = values[fromOffset + i];
        }
    }
}
