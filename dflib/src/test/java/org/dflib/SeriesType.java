package org.dflib;

import org.dflib.series.ArrayRangeSeries;
import org.dflib.series.ArraySeries;
import org.dflib.series.ColumnMappedSeries;
import org.dflib.series.RangeSeries;

public enum SeriesType {
    ARRAY, COLUMN_MAPPED, RANGE, ARRAY_RANGE;

    public <T> Series<T> createSeries(T... data) {
        switch (this) {

            // TODO: other types of supported Series
            case ARRAY:
                return new ArraySeries<>(data);
            case COLUMN_MAPPED:
                return new ColumnMappedSeries<>(new ArraySeries<>(data), v -> v);
            case RANGE:
                return new RangeSeries<>(new ArraySeries<>(data), 0, data.length);
            case ARRAY_RANGE:
                return new ArrayRangeSeries<>(data, 0, data.length);
            default:
                throw new IllegalStateException("Unknown series type: " + this);
        }
    }
}
