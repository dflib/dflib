package org.dflib.sort;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.Sorter;

import java.util.Comparator;

/**
 * A factory of comparators whose inputs are row position indices, and that are applied to individual columns.
 * Comparators are composable, so ordering by multiple columns can be specified.
 *
 * @deprecated in favor of static methods on {@link IntComparator}
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public final class Comparators {

    public static IntComparator of(DataFrame df, Sorter[] sorters) {
        return IntComparator.of(df, sorters);
    }

    public static IntComparator of(Series<?> s, Sorter[] sorters) {
        return IntComparator.of(s, sorters);
    }

    public static IntComparator of(DataFrame df, String[] columns, boolean[] ascending) {
        return IntComparator.of(df, columns, ascending);
    }

    public static IntComparator of(DataFrame df, int[] columns, boolean[] ascending) {
        return IntComparator.of(df, columns, ascending);
    }

    public static IntComparator of(IntSeries s, boolean ascending) {
        return IntComparator.of(s, ascending);
    }

    public static IntComparator of(LongSeries s, boolean ascending) {
        return IntComparator.of(s, ascending);
    }

    /**
     * @since 1.1.0
     */
    public static IntComparator of(FloatSeries s, boolean ascending) {
        return IntComparator.of(s, ascending);
    }

    public static IntComparator of(DoubleSeries s, boolean ascending) {
        return IntComparator.of(s, ascending);
    }

    public static IntComparator of(BooleanSeries s, boolean ascending) {
        return IntComparator.of(s, ascending);
    }

    public static <T> IntComparator of(Series<T> s, Comparator<? super T> comparator) {
        return IntComparator.of(s, comparator);
    }

    public static IntComparator of(Series<?> s, boolean ascending) {
        return of(s, ascending);
    }
}
