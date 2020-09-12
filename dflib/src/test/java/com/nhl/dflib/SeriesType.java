package com.nhl.dflib;

import com.nhl.dflib.series.*;

import static java.util.Arrays.asList;

public enum SeriesType {
    ARRAY, COLUMN_MAPPED, LIST, RANGE;

    public <T> Series<T> createSeries(T... data) {
        switch (this) {

            // TODO: other types of supported Series
            case ARRAY:
                return new ArraySeries<>(data);
            case COLUMN_MAPPED:
                return new ColumnMappedSeries<>(new ArraySeries<>(data), v -> v);
            case LIST:
                return new ListSeries<>(asList(data));
            case RANGE:
                return new RangeSeries<>(new ArraySeries<>(data), 0, data.length);
            default:
                throw new IllegalStateException("Unknown series type: " + this);
        }
    }
}
