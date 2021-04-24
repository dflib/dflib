package com.nhl.dflib.sort;

import com.nhl.dflib.*;
import com.nhl.dflib.series.IntArraySeries;

import java.util.function.Supplier;

/**
 * Sorting processor for DataFrames based on comparators whose inputs are row position indices.
 *
 * @see PerColumnComparators
 * @since 0.11
 */
public class PerColumnSorter {

    private final DataFrame dataFrame;
    private final Supplier<int[]> indexBuilder;

    public PerColumnSorter(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
        this.indexBuilder = () -> IndexSorter.rowNumberSequence(dataFrame.height());
    }

    public PerColumnSorter(DataFrame dataFrame, IntSeries rangeToSort) {
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
        return sort(PerColumnComparators.of(dataFrame.getColumn(column), ascending));
    }

    public DataFrame sort(int column, boolean ascending) {
        return sort(PerColumnComparators.of(dataFrame.getColumn(column), ascending));
    }

    public DataFrame sort(String[] columns, boolean[] ascending) {
        return sort(PerColumnComparators.of(dataFrame, columns, ascending));
    }

    public DataFrame sort(int[] columns, boolean[] ascending) {
        return sort(PerColumnComparators.of(dataFrame, columns, ascending));
    }

    public DataFrame sort(IntComparator comparator) {

        IntSeries sortedPositions = sortedPositions(comparator);

        int width = dataFrame.width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = dataFrame.getColumn(i).select(sortedPositions);
        }

        return new ColumnDataFrame(dataFrame.getColumnsIndex(), newColumnsData);
    }

    public <V extends Comparable<? super V>> DataFrame sort(RowToValueMapper<V> sortKeyExtractor) {
        return sort(PerColumnComparators.of(dataFrame, sortKeyExtractor));
    }

    public IntSeries sortedPositions(IntComparator comparator) {
        int[] mutableIndex = indexBuilder.get();
        IntTimSort.sort(mutableIndex, comparator);
        return new IntArraySeries(mutableIndex);
    }

}
