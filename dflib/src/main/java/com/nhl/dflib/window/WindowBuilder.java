package com.nhl.dflib.window;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.sort.Sorters;

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
                return partitioner != null ? rankPartitioned() : rankUnpartitioned();
        }
    }

    public IntSeries denseRank() {

        switch (dataFrame.height()) {
            case 0:
                return IntSeries.forInts();
            case 1:
                return IntSeries.forInts(1);
            default:
                return partitioner != null ? denseRankPartitioned() : denseRankUnpartitioned();
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

    private IntSeries rankPartitioned() {
        return sorter != null
                ? dataFrame.group(partitioner).sort(sorter).rank()
                : Ranker.sameRank(dataFrame.height());
    }

    private IntSeries rankUnpartitioned() {
        return sorter != null
                ? new Ranker(sorter).rank(dataFrame)
                : Ranker.sameRank(dataFrame.height());
    }

    private IntSeries denseRankPartitioned() {
        return sorter != null
                ? dataFrame.group(partitioner).sort(sorter).denseRank()
                : Ranker.sameRank(dataFrame.height());
    }

    private IntSeries denseRankUnpartitioned() {
        return sorter != null
                ? new DenseRanker(sorter).rank(dataFrame)
                : Ranker.sameRank(dataFrame.height());
    }

    private IntSeries rowNumberPartitioned() {
        GroupBy gb = dataFrame.group(partitioner);
        return sorter != null
                ? gb.sort(sorter).rowNumber()
                : gb.rowNumber();
    }

    private IntSeries rowNumberUnpartitioned() {
        return sorter != null
                ? new RowNumberer(sorter).rowNumber(dataFrame)
                : RowNumberer.sequence(dataFrame.height());
    }
}
