package com.nhl.dflib.sort;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.IntArraySeries;

import java.util.Comparator;

/**
 * @since 0.11
 */
public class SeriesSorter {

    public static <T> IntSeries sortedPositions(Series<T> s, Comparator<? super T> comparator) {
        int[] mutableIndex = rowNumberSequence(s.size());
        IntComparator intComparator = (i1, i2) -> comparator.compare(s.get(i1), s.get(i2));
        IntTimSort.sort(mutableIndex, intComparator);
        return new IntArraySeries(mutableIndex);
    }

    public static int[] rowNumberSequence(int h) {
        int[] rn = new int[h];
        for (int i = 0; i < h; i++) {
            rn[i] = i;
        }

        return rn;
    }
}
