package org.dflib.concat;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.series.ArraySeries;
import org.dflib.series.IntArraySeries;

import static java.util.Arrays.asList;

public class SeriesConcat {

    public static <T> Series<T> concat(Series<T>... concat) {
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

    /**
     * @since 0.6
     */
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

    public static IntSeries intConcat(IntSeries... concat) {
        return intConcat(asList(concat));
    }

    public static IntSeries intConcat(Iterable<IntSeries> concat) {
        int h = 0;
        int total = 0;
        for (IntSeries s : concat) {
            h += s.size();
            total++;
        }

        if (total == 1) {
            return concat.iterator().next();
        }

        int[] data = new int[h];
        int offset = 0;
        for (IntSeries s : concat) {
            int len = s.size();
            s.copyToInt(data, 0, offset, len);
            offset += len;
        }

        return new IntArraySeries(data);
    }
}
