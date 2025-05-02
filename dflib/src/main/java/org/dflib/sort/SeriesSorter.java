package org.dflib.sort;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.Sorter;

import java.util.Comparator;

/**
 * @deprecated unused internally. {@link IntComparator} now does what SeriesSorter used to do.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class SeriesSorter<T> {

    private final Series<T> s;

    public SeriesSorter(Series<T> s) {
        this.s = s;
    }

    public static int[] rowNumberSequence(int h) {
        return IntComparators.sequence(h);
    }

    public IntSeries sortIndex(IntComparator comparator) {
        return comparator.sortIndex(s.size());
    }

    public IntSeries sortIndex(Comparator<? super T> comparator) {
        return s.sortIndex(comparator);
    }

    public IntSeries sortIndex(Sorter... sorters) {
        return IntComparator.of(s, sorters).sortIndex(s.size());
    }

    public Series<T> sort(Sorter... sorters) {
        return sorters.length == 0 ? s : s.select(sortIndex(sorters));
    }

    public Series<T> sort(Comparator<? super T> comparator) {
        return s.sort(comparator);
    }
}
