package com.nhl.dflib.concat;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;

public class SeriesConcat {

    public static <T> Series<T> concat(Iterable<Series<T>> concat) {
        int h = 0;
        for (Series<? extends T> s : concat) {
            h += s.size();
        }

        T[] data = (T[]) new Object[h];
        int offset = 0;
        for (Series<? extends T> s : concat) {
            int len = s.size();
            s.copyTo(data, 0, offset, len);
            offset += len;
        }

        return new ArraySeries<>(data);
    }
}
