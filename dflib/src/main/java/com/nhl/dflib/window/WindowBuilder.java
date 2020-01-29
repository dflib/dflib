package com.nhl.dflib.window;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.row.DataFrameRowProxy;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.sort.IndexSorter;
import com.nhl.dflib.sort.Sorters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * A mutable builder of a window function. Returned by {@link DataFrame#over()} method.
 *
 * @since 0.8
 */
public class WindowBuilder {

    private DataFrame dataFrame;
    private Hasher partitioner;
    private Comparator<RowProxy> sorter;

    public WindowBuilder(DataFrame dataFrame) {
        this.dataFrame = Objects.requireNonNull(dataFrame);
    }

    public WindowBuilder partitioned(Hasher partitioner) {
        this.partitioner = Objects.requireNonNull(partitioner);
        return this;
    }

    public WindowBuilder partitioned(String... columns) {

        int len = columns.length;
        if (len == 0) {
            throw new IllegalArgumentException("No partitioning columns specified");
        }

        Hasher partitioner = Hasher.forColumn(columns[0]);
        for (int i = 1; i < columns.length; i++) {
            partitioner = partitioner.and(columns[i]);
        }

        this.partitioner = partitioner;
        return this;
    }

    public WindowBuilder partitioned(int... columns) {
        int len = columns.length;
        if (len == 0) {
            throw new IllegalArgumentException("No partitioning columns specified");
        }

        Hasher partitioner = Hasher.forColumn(columns[0]);
        for (int i = 1; i < columns.length; i++) {
            partitioner = partitioner.and(columns[i]);
        }

        this.partitioner = partitioner;
        return this;
    }

    public <V extends Comparable<? super V>> WindowBuilder sorted(RowToValueMapper<V> sortKeyExtractor) {
        this.sorter = Sorters.sorter(sortKeyExtractor);
        return this;
    }

    public WindowBuilder sorted(String column, boolean ascending) {
        this.sorter = Sorters.sorter(dataFrame.getColumnsIndex(), column, ascending);
        return this;
    }

    public WindowBuilder sorted(int column, boolean ascending) {
        this.sorter = Sorters.sorter(column, ascending);
        return this;
    }

    public WindowBuilder sorted(String[] columns, boolean[] ascending) {
        this.sorter = Sorters.sorter(dataFrame.getColumnsIndex(), columns, ascending);
        return this;
    }

    public WindowBuilder sorted(int[] columns, boolean[] ascending) {
        this.sorter = Sorters.sorter(columns, ascending);
        return this;
    }

    public IntSeries rank() {
        switch (dataFrame.height()) {
            case 0:
                return IntSeries.forInts();
            case 1:
                return IntSeries.forInts(1);
            default:
                return sorter != null ? rankSorted() : rankUnsorted();
        }
    }

    public IntSeries rowNumber() {
        switch (dataFrame.height()) {
            case 0:
                return IntSeries.forInts();
            case 1:
                return IntSeries.forInts(1);
            default:
                return partitioner != null ? rowNumberPartitioned() : rowNumberUnpartitioned();
        }
    }

    private IntSeries rankSorted() {
        return partitioner != null ? rankPartitionedSorted() : rankUnpartitionedSorted();
    }

    private IntSeries rankPartitionedSorted() {
        throw new UnsupportedOperationException("TODO");
    }

    private IntSeries rankUnpartitionedSorted() {

        IntSeries sortIndex = new IndexSorter(dataFrame).sortIndex(sorter);
        DataFrameRowProxy pproxy = new DataFrameRowProxy(dataFrame);
        DataFrameRowProxy rproxy = new DataFrameRowProxy(dataFrame);
        int len = dataFrame.height();

        int[] rank = new int[len];

        for (int i = 0; i < len; i++) {

            int row = sortIndex.getInt(i);

            if (i == 0) {
                rank[row] = 1;
            } else {
                int prow = sortIndex.getInt(i - 1);
                rank[row] = sorter.compare(rproxy.rewind(row), pproxy.rewind(prow)) == 0 ? rank[prow] : i + 1;
            }
        }

        return IntSeries.forInts(rank);
    }

    private IntSeries rankUnsorted() {
        // no sort order means each row is equivalent from the ranking perspective, so return a Series of 1's
        int[] ints = new int[dataFrame.height()];
        Arrays.fill(ints, 1);
        return IntSeries.forInts(ints);
    }

    private IntSeries rowNumberPartitioned() {
        GroupBy gb = dataFrame.group(partitioner);
        return sorter != null
                ? gb.sort(sorter).rowNumber()
                : gb.rowNumber();
    }

    private IntSeries rowNumberUnpartitioned() {
        return sorter != null
                ? rowNumberUnpartitionedSorted()
                : RowNumber.getNumbers(dataFrame.height());
    }

    private IntSeries rowNumberUnpartitionedSorted() {

        // note how we are calling "sortIndex(sortIndex(..))"
        // 1. the first call produces a Series where Series positions correspond to the new positions of the old rows
        //   in the sorted Series, while values are old positions before sorting.
        // 2. the second call inverts this mapping, producing a Series where Series positions correspond to the original
        //   value positions, and values are their positions in the imaginary sorted data set.

        // (1) is good for producing a sorted Series or DataFrame, (2) is good for producing row numbers that follow
        // the sorting.. We have case (2) here.

        IntSeries rowPositions = new IndexSorter(dataFrame).sortIndex(sorter).sortIndexInt();
        return (IntSeries) RowNumber.getNumbers(dataFrame.height()).select(rowPositions);
    }
}
