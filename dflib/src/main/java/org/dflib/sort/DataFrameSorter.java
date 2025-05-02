package org.dflib.sort;

import org.dflib.IntSeries;
import org.dflib.series.IntArraySeries;

/**
 * @deprecated in favor of {@link IntComparator#sortIndex(int)} and {@link IntSeries#sortInt(IntComparator)}.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class DataFrameSorter {

    /**
     * Creates an int sequence of a given size, and then sorts it with the provided comparator.
     *
     * @deprecated in favor of {@link IntComparator#sortIndex(int)}
     */
    public static IntSeries sort(IntComparator comparator, int size) {
        return doSort(comparator, rowNumberSequence(size));
    }

    /**
     * Sorts the Series it with the provided comparator.
     *
     * @deprecated in favor of {@link IntSeries#sortInt(IntComparator)}
     */
    public static IntSeries sort(IntComparator comparator, IntSeries ints) {

        // copy range to avoid modification of the source list
        int len = ints.size();
        int[] index = new int[len];
        ints.copyToInt(index, 0, 0, len);

        return doSort(comparator, index);
    }

    private static IntSeries doSort(IntComparator comparator, int[] mutableIndex) {
        IntTimSort.sort(mutableIndex, comparator);
        return new IntArraySeries(mutableIndex);
    }

    private static int[] rowNumberSequence(int h) {
        int[] rn = new int[h];
        for (int i = 0; i < h; i++) {
            rn[i] = i;
        }

        return rn;
    }
}
