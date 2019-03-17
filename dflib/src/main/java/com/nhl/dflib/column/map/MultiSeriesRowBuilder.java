package com.nhl.dflib.column.map;

import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.RowBuilder;
import com.nhl.dflib.series.ArraySeries;

public class MultiSeriesRowBuilder implements RowBuilder {

    private Index index;
    private Object[][] data;
    private int row;

    public MultiSeriesRowBuilder(Index index, int height) {
        this.index = index;
        this.data = new Object[index.size()][height];
    }

    public Series<?>[] getData() {
        int w = index.size();
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(data[i]);
        }

        return series;
    }

    @Override
    public Index getIndex() {
        return index;
    }

    @Override
    public void set(String columnName, Object value) {
        data[index.position(columnName).ordinal()][row] = value;
    }

    @Override
    public void set(int columnPos, Object value) {
        data[columnPos][row] = value;
    }

    @Override
    public void setRange(Object[] values, int fromOffset, int toOffset, int len) {

        if (len + toOffset > index.span()) {
            throw new IllegalArgumentException("Provided values won't fit in the row: "
                    + (len + toOffset)
                    + " > "
                    + index.span());
        }

        for (int i = 0; i < len; i++) {
            data[toOffset + i][row] = values[fromOffset + i];
        }
    }

    public void reset() {
        row++;
    }
}
