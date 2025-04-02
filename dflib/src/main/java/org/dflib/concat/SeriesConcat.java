package org.dflib.concat;

import org.dflib.IntSeries;
import org.dflib.Series;

import java.util.stream.StreamSupport;

public class SeriesConcat {

    /**
     * Concatenates multiple Series of various length into one.
     */
    public static <T> Series<T> concat(Series<T>... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return Series.of();
        } else if (clen == 1) {
            return concat[0];
        }

        Series[] other = new Series[clen - 1];
        System.arraycopy(concat, 1, other, 0, clen - 1);

        return concat[0].concat(other);
    }

    /**
     * @deprecated unused internally; use either {@link #concat(Series[])} or {@link Series#concat(Series[])}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static <T> Series<T> concat(Iterable<Series<T>> concat) {
        return concat(StreamSupport.stream(concat.spliterator(), false).toArray(Series[]::new));
    }

    public static IntSeries intConcat(IntSeries... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return Series.ofInt();
        } else if (clen == 1) {
            return concat[0];
        }

        IntSeries[] other = new IntSeries[clen - 1];
        System.arraycopy(concat, 1, other, 0, clen - 1);

        return concat[0].concatInt(other);
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

        return Series.ofInt(data);
    }
}
