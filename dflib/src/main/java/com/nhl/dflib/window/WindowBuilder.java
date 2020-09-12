package com.nhl.dflib.window;

import com.nhl.dflib.*;
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
                return partitioner != null ? rankPartitioned() : rankUnPartitioned();
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

    /**
     * @since 0.9
     */
    public <T> Series<T> shift(String column, int offset) {
        return shift(column, offset, null);
    }

    /**
     * @since 0.9
     */
    public <T> Series<T> shift(String column, int offset, T filler) {
        int pos = dataFrame.getColumnsIndex().position(column);
        return shift(pos, offset, filler);
    }

    /**
     * @since 0.9
     */
    public <T> Series<T> shift(int column, int offset) {
        return shift(column, offset, null);
    }

    /**
     * @since 0.9
     */
    public <T> Series<T> shift(int column, int offset, T filler) {
        switch (dataFrame.height()) {
            case 0:
                return Series.forData();
            case 1:
                return Series.forData(filler);
            default:
                // use a "single group" partitioner if not set
                // TODO: this likely creates performance overhead for such a simple case
                Hasher partitioner = this.partitioner != null ? this.partitioner : r -> "k";
                GroupBy gb = dataFrame.group(partitioner);
                return sorter != null ? gb.sort(sorter).shift(column, offset, filler) : gb.shift(column, offset, filler);
        }
    }

    private IntSeries rankPartitioned() {
        return sorter != null
                ? dataFrame.group(partitioner).sort(sorter).rank()
                : Ranker.sameRank(dataFrame.height());
    }

    private IntSeries rankUnPartitioned() {
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
                ? RowNumberer.rowNumber(dataFrame, sorter)
                : RowNumberer.sequence(dataFrame.height());
    }
}
