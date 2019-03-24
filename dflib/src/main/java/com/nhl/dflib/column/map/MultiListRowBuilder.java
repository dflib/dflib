package com.nhl.dflib.column.map;

import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.RowBuilder;
import com.nhl.dflib.series.ArraySeries;

import java.util.ArrayList;
import java.util.List;

public class MultiListRowBuilder implements RowBuilder {

    private Index index;
    private List[] data;
    private int row;

    public MultiListRowBuilder(Index index, int estimatedCapacity) {
        this.index = index;
        this.row = -1;
        this.data = new List[index.size()];

        int capacity = estimatedCapacity > 1 ? estimatedCapacity : 10;
        for (int i = 0; i < data.length; i++) {
            data[i] = new ArrayList<>(capacity);
        }
    }

    public Series<?>[] getData() {
        int w = index.size();
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(data[i].toArray());
        }

        return series;
    }

    @Override
    public Index getIndex() {
        return index;
    }

    @Override
    public void set(String columnName, Object value) {
        data[index.position(columnName).ordinal()].set(row, value);
    }

    @Override
    public void set(int columnPos, Object value) {
        data[columnPos].set(row, value);
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
            data[toOffset + i].set(row, values[fromOffset + i]);
        }
    }

    public void startRow() {
        for (int i = 0; i < data.length; i++) {
            data[i].add(null);
        }
        row++;
    }
}
