package org.dflib.concat;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.union.SeriesUnion;

import java.util.stream.StreamSupport;

/**
 * @deprecated in favor of {@link SeriesUnion}
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class SeriesConcat {

    /**
     * Concatenates multiple Series of various length into one.
     */
    @SafeVarargs
    public static <T> Series<T> concat(Series<T>... concat) {
        return SeriesUnion.of(concat);
    }

    public static <T> Series<T> concat(Iterable<Series<T>> concat) {
        return SeriesUnion.of(StreamSupport.stream(concat.spliterator(), false).toArray(Series[]::new));
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
        return intConcat(StreamSupport.stream(concat.spliterator(), false).toArray(IntSeries[]::new));
    }
}
