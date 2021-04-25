package com.nhl.dflib.sort;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.series.IntArraySeries;

import java.util.function.Supplier;

/**
 * Sorting processor for DataFrames.
 *
 * @see Comparators
 * @since 0.11
 */
public class DataFrameSorter {

    private final DataFrame dataFrame;
    private final Supplier<int[]> indexBuilder;

    public DataFrameSorter(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
        this.indexBuilder = () -> SeriesSorter.rowNumberSequence(dataFrame.height());
    }

    public DataFrameSorter(DataFrame dataFrame, IntSeries rangeToSort) {
        this.dataFrame = dataFrame;

        // copy range to avoid modification of the source list
        this.indexBuilder = () -> {
            int len = rangeToSort.size();
            int[] data = new int[len];
            rangeToSort.copyToInt(data, 0, 0, len);
            return data;
        };
    }

    public DataFrame sort(String column, boolean ascending) {
        return sort(Comparators.of(dataFrame.getColumn(column), ascending));
    }

    public DataFrame sort(int column, boolean ascending) {
        return sort(Comparators.of(dataFrame.getColumn(column), ascending));
    }

    public DataFrame sort(String[] columns, boolean[] ascending) {
        return sort(Comparators.of(dataFrame, columns, ascending));
    }

    public DataFrame sort(int[] columns, boolean[] ascending) {
        return sort(Comparators.of(dataFrame, columns, ascending));
    }

    public DataFrame sort(IntComparator comparator) {
        IntSeries sortedPositions = sortedPositions(comparator);
        return dataFrame.selectRows(sortedPositions);
    }

    public <V extends Comparable<? super V>> DataFrame sort(RowToValueMapper<V> sortKeyExtractor) {
        return sort(Comparators.of(dataFrame, sortKeyExtractor));
    }

    public DataFrame sort(Sorter... sorters) {
        return sort(Comparators.of(dataFrame, sorters));
    }

    public IntSeries sortedPositions(IntComparator comparator) {
        int[] mutableIndex = indexBuilder.get();
        IntTimSort.sort(mutableIndex, comparator);
        return new IntArraySeries(mutableIndex);
    }
}
