package com.nhl.dflib.window;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.agg.WindowAggregator;
import com.nhl.dflib.agg.WindowMapper;
import com.nhl.dflib.series.IntSequenceSeries;
import com.nhl.dflib.sort.Comparators;
import com.nhl.dflib.sort.DataFrameSorter;
import com.nhl.dflib.sort.IntComparator;

import java.util.Objects;

/**
 * A mutable builder of a window function. Returned by {@link DataFrame#over()} method.
 *
 * @since 0.8
 */
public class WindowBuilder {

    private final DataFrame dataFrame;
    private Hasher partitioner;
    private IntComparator sorter;

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

    /**
     * @deprecated since 0.12 as sorting by RowToValueMapper is redundant, and can be expressed as a Sorter.
     */
    @Deprecated
    public <V extends Comparable<? super V>> WindowBuilder sorted(RowToValueMapper<V> sortKeyExtractor) {
        this.sorter = Comparators.of(dataFrame, sortKeyExtractor);
        return this;
    }

    /**
     * @since 0.11
     */
    public WindowBuilder sorted(Sorter... sorters) {
        this.sorter = sorters.length == 0 ? null : Comparators.of(dataFrame, sorters);
        return this;
    }

    public WindowBuilder sorted(String column, boolean ascending) {
        this.sorter = Comparators.of(dataFrame.getColumn(column), ascending);
        return this;
    }

    public WindowBuilder sorted(int column, boolean ascending) {
        this.sorter = Comparators.of(dataFrame.getColumn(column), ascending);
        return this;
    }

    public WindowBuilder sorted(String[] columns, boolean[] ascending) {
        this.sorter = Comparators.of(dataFrame, columns, ascending);
        return this;
    }

    public WindowBuilder sorted(int[] columns, boolean[] ascending) {
        this.sorter = Comparators.of(dataFrame, columns, ascending);
        return this;
    }

    /**
     * @since 0.11
     */
    public DataFrame agg(Exp<?>... aggregators) {
        return partitioner != null ? aggPartitioned(aggregators) : aggUnPartitioned(aggregators);
    }

    /**
     * Applies an aggregating expression to each DataFrame row, passing it a window associated with the current row.
     *
     * @since 0.14
     */
    public <T> Series<T> mapColumn(Exp<T> windowAggregator) {
        return partitioner != null ? mapPartitioned(windowAggregator) : mapUnPartitioned(windowAggregator);
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
                return partitioner != null ? denseRankPartitioned() : denseRankUnPartitioned();
        }
    }

    public IntSeries rowNumber() {
        switch (dataFrame.height()) {
            case 0:
                return IntSeries.forInts();
            case 1:
                return IntSeries.forInts(1);
            default:
                return partitioner != null ? rowNumberPartitioned() : rowNumberUnPartitioned();
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

        if (offset == 0) {
            return dataFrame.getColumn(column);
        }

        switch (dataFrame.height()) {
            case 0:
                return Series.forData();
            case 1:
                return Series.forData(filler);
            default:
                return partitioner != null
                        ? shiftPartitioned(column, offset, filler)
                        : shiftUnPartitioned(column, offset, filler);
        }
    }

    private <T> Series<T> shiftPartitioned(int column, int offset, T filler) {
        GroupBy gb = dataFrame.group(partitioner);
        return sorter != null ? gb.sort(sorter).shift(column, offset, filler) : gb.shift(column, offset, filler);
    }

    private <T> Series<T> shiftUnPartitioned(int column, int offset, T filler) {
        if (sorter != null) {
            IntSeries index = new IntSequenceSeries(0, dataFrame.height());
            IntSeries sortedPositions = new DataFrameSorter(dataFrame, index).sortIndex(sorter);
            Series<T> s = dataFrame.getColumn(column);
            return s.select(sortedPositions).shift(offset, filler).select(sortedPositions.sortIndexInt());
        } else {
            Series<T> s = dataFrame.getColumn(column);
            return s.shift(offset, filler);
        }
    }

    private DataFrame aggPartitioned(Exp<?>... aggregators) {
        GroupBy gb = sorter != null
                ? dataFrame.group(partitioner).sort(sorter)
                : dataFrame.group(partitioner);

        return WindowAggregator.aggPartitioned(gb, aggregators);
    }

    private DataFrame aggUnPartitioned(Exp<?>... aggregators) {
        DataFrame df = sorter != null
                // TODO: create a DataFrame sort method that takes IntComparator
                ? new DataFrameSorter(dataFrame).sort(sorter)
                : dataFrame;

        return WindowAggregator.agg(df, aggregators);
    }

    private <T> Series<T> mapPartitioned(Exp<T> aggregator) {
        GroupBy gb = sorter != null
                ? dataFrame.group(partitioner).sort(sorter)
                : dataFrame.group(partitioner);

        return WindowMapper.mapPartitioned(gb, aggregator);
    }

    private <T> Series<T> mapUnPartitioned(Exp<T> aggregator) {
        DataFrame df = sorter != null
                // TODO: create a DataFrame sort method that takes IntComparator
                ? new DataFrameSorter(dataFrame).sort(sorter)
                : dataFrame;

        return WindowMapper.map(df, aggregator);
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

    private IntSeries denseRankUnPartitioned() {
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

    private IntSeries rowNumberUnPartitioned() {
        return sorter != null
                ? RowNumberer.rowNumber(dataFrame, sorter)
                : RowNumberer.sequence(dataFrame.height());
    }
}
