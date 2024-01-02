package org.dflib.sort;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.series.ArraySeries;
import org.dflib.series.IntArraySeries;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @since 0.11
 */
public class SeriesSorter<T> {

    private final Series<T> s;

    public SeriesSorter(Series<T> s) {
        this.s = s;
    }

    public static int[] rowNumberSequence(int h) {
        int[] rn = new int[h];
        for (int i = 0; i < h; i++) {
            rn[i] = i;
        }

        return rn;
    }

    public IntSeries sortIndex(IntComparator comparator) {
        int[] mutableIndex = SeriesSorter.rowNumberSequence(s.size());
        IntTimSort.sort(mutableIndex, comparator);
        return new IntArraySeries(mutableIndex);
    }

    public IntSeries sortIndex(Comparator<? super T> comparator) {
        return sortIndex(Comparators.of(s, comparator));
    }

    public IntSeries sortIndex(Sorter... sorters) {
        return sortIndex(Comparators.of(s, sorters));
    }

    public Series<T> sort(Sorter... sorters) {
        return sorters.length == 0 ? s : s.select(sortIndex(sorters));
    }

    public Series<T> sort(Comparator<? super T> comparator) {

        // TODO: should we use "sortIndex" for consistency? It will be marginally slower
        //  than this specialized impl (an extra int[] allocation)

        int size = s.size();
        T[] sorted = (T[]) new Object[size];
        s.copyTo(sorted, 0, 0, size);
        Arrays.sort(sorted, comparator);
        return new ArraySeries<>(sorted);
    }
}
