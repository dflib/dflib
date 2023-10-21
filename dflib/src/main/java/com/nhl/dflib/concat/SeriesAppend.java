package com.nhl.dflib.concat;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;

/**
 * @since 0.18
 */
public class SeriesAppend {

    public static <T> Series<T> append(Series<T> s, T value) {
        int len = s.size() + 1;
        T[] data = (T[]) new Object[len];
        s.copyTo(data, 0, 0, s.size());
        data[len - 1] = value;
        return new ArraySeries<>(data);
    }
}
