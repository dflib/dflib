package org.dflib.window;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.GroupBy;
import org.dflib.Hasher;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.agg.DataFrameAggregator;
import org.dflib.agg.GroupByAggregator;
import org.dflib.agg.WindowMapper;
import org.dflib.exp.Exps;
import org.dflib.series.IntSequenceSeries;
import org.dflib.sort.Comparators;
import org.dflib.sort.DataFrameSorter;
import org.dflib.sort.IntComparator;

import java.util.Objects;
import java.util.Set;

/**
 * A mutable builder of a window function. Returned by {@link DataFrame#over()} method.
 *
 * @since 0.8
 */
public class WindowBuilder {

    private final DataFrame source;
    private Hasher partitioner;
    private IntComparator sorter;
    private WindowRange range;

    public WindowBuilder(DataFrame source) {
        this.source = Objects.requireNonNull(source);
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

        Hasher partitioner = Hasher.of(columns[0]);
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

        Hasher partitioner = Hasher.of(columns[0]);
        for (int i = 1; i < columns.length; i++) {
            partitioner = partitioner.and(columns[i]);
        }

        this.partitioner = partitioner;
        return this;
    }

    /**
     * @since 0.11
     */
    public WindowBuilder sorted(Sorter... sorters) {
        this.sorter = sorters.length == 0 ? null : Comparators.of(source, sorters);
        return this;
    }

    public WindowBuilder sorted(String column, boolean ascending) {
        this.sorter = Comparators.of(source.getColumn(column), ascending);
        return this;
    }

    public WindowBuilder sorted(int column, boolean ascending) {
        this.sorter = Comparators.of(source.getColumn(column), ascending);
        return this;
    }

    public WindowBuilder sorted(String[] columns, boolean[] ascending) {
        this.sorter = Comparators.of(source, columns, ascending);
        return this;
    }

    public WindowBuilder sorted(int[] columns, boolean[] ascending) {
        this.sorter = Comparators.of(source, columns, ascending);
        return this;
    }

    /**
     * Sets an explicit row range for the window. The default is {@link WindowRange#all}. Only has effect on the
     * {@link #mapColumn(Exp)} operation.
     *
     * @since 0.14
     */
    //  TODO: which operations other than mapColumn require a range? Does is make sense for rank or shift?
    public WindowBuilder range(WindowRange range) {
        this.range = range;
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

        switch (source.height()) {
            case 0:
                return Series.ofInt();
            case 1:
                return Series.ofInt(1);
            default:
                return partitioner != null ? rankPartitioned() : rankUnPartitioned();
        }
    }

    public IntSeries denseRank() {

        switch (source.height()) {
            case 0:
                return Series.ofInt();
            case 1:
                return Series.ofInt(1);
            default:
                return partitioner != null ? denseRankPartitioned() : denseRankUnPartitioned();
        }
    }

    public IntSeries rowNumber() {
        switch (source.height()) {
            case 0:
                return Series.ofInt();
            case 1:
                return Series.ofInt(1);
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
        int pos = source.getColumnsIndex().position(column);
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
            return source.getColumn(column);
        }

        switch (source.height()) {
            case 0:
                return Series.of();
            case 1:
                return Series.of(filler);
            default:
                return partitioner != null
                        ? shiftPartitioned(column, offset, filler)
                        : shiftUnPartitioned(column, offset, filler);
        }
    }

    private <T> Series<T> shiftPartitioned(int column, int offset, T filler) {
        GroupBy gb = source.group(partitioner);
        return sorter != null ? gb.sort(sorter).shift(column, offset, filler) : gb.shift(column, offset, filler);
    }

    private <T> Series<T> shiftUnPartitioned(int column, int offset, T filler) {
        if (sorter != null) {
            IntSeries index = new IntSequenceSeries(0, source.height());
            IntSeries sortedPositions = DataFrameSorter.sort(sorter, index);
            Series<T> s = source.getColumn(column);
            return s.select(sortedPositions).shift(offset, filler).select(sortedPositions.sortIndexInt());
        } else {
            Series<T> s = source.getColumn(column);
            return s.shift(offset, filler);
        }
    }

    private DataFrame aggPartitioned(Exp<?>... aggregators) {
        GroupBy gb = sorter != null
                ? source.group(partitioner).sort(sorter)
                : source.group(partitioner);

        Series<?>[] rowPerGroup = GroupByAggregator.agg(gb, aggregators);
        int h = gb.getSource().height();
        int aggW = rowPerGroup.length;

        Object[][] data = new Object[aggW][h];

        for (int i = 0; i < aggW; i++) {
            data[i] = new Object[h];
        }

        int gi = 0;
        for (Object key : gb.getGroupKeys()) {

            IntSeries index = gb.getGroupIndex(key);
            int ih = index.size();

            for (int i = 0; i < aggW; i++) {

                // fill positions in the index with the singe aggregated value
                Object val = rowPerGroup[i].get(gi);
                for (int j = 0; j < ih; j++) {
                    data[i][index.getInt(j)] = val;
                }
            }

            gi++;
        }

        Series<?>[] columns = new Series[aggW];
        for (int i = 0; i < aggW; i++) {
            columns[i] = Series.of(data[i]);
        }

        return new ColumnDataFrame(null, Exps.index(source, aggregators), columns);
    }

    private DataFrame aggUnPartitioned(Exp<?>... aggregators) {
        DataFrame df = sorter != null
                ? source.rows(DataFrameSorter.sort(sorter, source.height())).select()
                : source;

        int h = df.height();
        int w = aggregators.length;

        Series<?>[] oneRowSeries = DataFrameAggregator.agg(df, aggregators);

        // expand each column to the height of the original DataFrame
        Series<?>[] expandedColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            expandedColumns[i] = Series.ofVal(oneRowSeries[i].get(0), h);
        }

        return new ColumnDataFrame(null, Exps.index(df, aggregators), expandedColumns);
    }

    private <T> Series<T> mapPartitioned(Exp<T> aggregator) {
        GroupBy gb = sorter != null
                ? source.group(partitioner).sort(sorter)
                : source.group(partitioner);

        return WindowMapper.mapPartitioned(gb, aggregator, resolveRange());
    }

    private <T> Series<T> mapUnPartitioned(Exp<T> aggregator) {
        DataFrame sorted = sorter != null
                ? source.rows(DataFrameSorter.sort(sorter, source.height())).select()
                : source;

        return WindowMapper.map(sorted, aggregator, resolveRange());
    }

    private WindowRange resolveRange() {
        return range != null ? range : WindowRange.all;
    }

    private IntSeries rankPartitioned() {
        return sorter != null
                ? source.group(partitioner).sort(sorter).rank()
                // no sort order means each row is equivalent from the ranking perspective, so return a Series of 1's
                : Ranker.sameRank(source.height());
    }

    private IntSeries rankUnPartitioned() {
        return sorter != null
                ? new Ranker(sorter).rank(source)
                : Ranker.sameRank(source.height());
    }

    private IntSeries denseRankPartitioned() {
        return sorter != null
                ? source.group(partitioner).sort(sorter).denseRank()
                : Ranker.sameRank(source.height());
    }

    private IntSeries denseRankUnPartitioned() {
        return sorter != null
                ? new DenseRanker(sorter).rank(source)
                : Ranker.sameRank(source.height());
    }

    private IntSeries rowNumberPartitioned() {
        GroupBy gb = sorter != null
                ? source.group(partitioner).sort(sorter)
                : source.group(partitioner);

        Set<Object> groupKeys = gb.getGroupKeys();
        int len = groupKeys.size();
        IntSeries[] groupIndices = new IntSeries[len];
        int i = 0;
        for (Object key : groupKeys) {
            groupIndices[i++] = gb.getGroupIndex(key);
        }

        return RowNumberer.rowNumber(source, groupIndices);
    }

    private IntSeries rowNumberUnPartitioned() {
        return sorter != null
                ? RowNumberer.rowNumber(source, sorter)
                : RowNumberer.sequence(source.height());
    }
}
