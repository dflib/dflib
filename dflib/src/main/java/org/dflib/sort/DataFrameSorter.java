package org.dflib.sort;

import org.dflib.IntSeries;
import org.dflib.series.IntArraySeries;

/**
 * Sorting processor for DataFrames.
 *
 * @see Comparators
 */
public class DataFrameSorter {

    public static IntSeries sort(IntComparator comparator, int height) {
        return doSort(comparator, SeriesSorter.rowNumberSequence(height));
    }

    public static IntSeries sort(IntComparator comparator, IntSeries range) {

        // copy range to avoid modification of the source list
        int len = range.size();
        int[] index = new int[len];
        range.copyToInt(index, 0, 0, len);

        return doSort(comparator, index);
    }

    private static IntSeries doSort(IntComparator comparator, int[] mutableIndex) {
        IntTimSort.sort(mutableIndex, comparator);
        return new IntArraySeries(mutableIndex);
    }
}
