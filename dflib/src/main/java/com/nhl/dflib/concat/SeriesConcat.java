package com.nhl.dflib.concat;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;

public class SeriesConcat {

    public static <T> Series<T> concat(Series<? extends T>... concat) {

        int h = 0;
        int n = concat.length;
        for (Series<?> s : concat) {
            h += s.size();
        }

        T[] data = (T[]) new Object[h];
        for (int i = 0, ai = 0; i < n; i++) {
            int len = concat[i].size();
            concat[i].copyTo(data, 0, ai, len);
            ai += len;
        }

        return new ArraySeries<>(data);
    }
}
