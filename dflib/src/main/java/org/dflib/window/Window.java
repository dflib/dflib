package org.dflib.window;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.GroupBy;
import org.dflib.Hasher;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.agg.RangeAggregator;
import org.dflib.exp.Exps;
import org.dflib.series.IntSequenceSeries;
import org.dflib.slice.ColumnSetMerger;
import org.dflib.slice.FixedColumnSetIndex;
import org.dflib.sort.Comparators;
import org.dflib.sort.DataFrameSorter;
import org.dflib.sort.IntComparator;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A mutable builder of a window function. Returned by {@link DataFrame#over()} method.
 *
 * @since 1.0.0-M22
 */
public class Window {

    private final DataFrame source;

    private Hasher partitioner;
    private IntComparator sorter;
    private WindowRange range;
    private FixedColumnSetIndex columnSetIndex;

    public Window(DataFrame source) {
        this.source = Objects.requireNonNull(source);
    }

    /**
     * Returns the unchanged original DataFrame that was used to build the window, that does not have GroupBy sorting,
     * trimming and other changes applied.
     *
     * @since 1.0.0-M22
     */
    public DataFrame getSource() {
        return source;
    }

    /**
     * Specifies the columns of the aggregation or select result.
     *
     * @since 1.0.0-M22
     */
    public Window cols(Predicate<String> colsPredicate) {
        Index srcIndex = source.getColumnsIndex();
        this.columnSetIndex = FixedColumnSetIndex.of(srcIndex, srcIndex.positions(colsPredicate));
        return this;
    }

    /**
     * Specifies the columns of the aggregation or select result.
     *
     * @since 1.0.0-M22
     */
    public Window cols(String... cols) {
        this.columnSetIndex = FixedColumnSetIndex.of(cols);
        return this;
    }

    /**
     * Specifies the columns for the select result.
     *
     * @since 1.0.0-M22
     */
    public Window cols(int... cols) {
        Index srcIndex = source.getColumnsIndex();
        this.columnSetIndex = FixedColumnSetIndex.of(srcIndex, cols);
        return this;
    }

    /**
     * @since 1.0.0-M22
     */
    public Window colsExcept(String... cols) {
        return cols(source.getColumnsIndex().positionsExcept(cols));
    }

    /**
     * @since 1.0.0-M22
     */
    public Window colsExcept(int... cols) {
        return cols(source.getColumnsIndex().positionsExcept(cols));
    }

    /**
     * @since 1.0.0-M22
     */
    public Window colsExcept(Predicate<String> colsPredicate) {
        return cols(colsPredicate.negate());
    }


    public Window partitioned(Hasher partitioner) {
        this.partitioner = Objects.requireNonNull(partitioner);
        return this;
    }

    public Window partitioned(String... columns) {

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

    public Window partitioned(int... columns) {
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
    public Window sorted(Sorter... sorters) {
        this.sorter = sorters.length == 0 ? null : Comparators.of(source, sorters);
        return this;
    }

    public Window sorted(String column, boolean ascending) {
        this.sorter = Comparators.of(source.getColumn(column), ascending);
        return this;
    }

    public Window sorted(int column, boolean ascending) {
        this.sorter = Comparators.of(source.getColumn(column), ascending);
        return this;
    }

    public Window sorted(String[] columns, boolean[] ascending) {
        this.sorter = Comparators.of(source, columns, ascending);
        return this;
    }

    public Window sorted(int[] columns, boolean[] ascending) {
        this.sorter = Comparators.of(source, columns, ascending);
        return this;
    }

    /**
     * Sets an explicit row range for the window. The default is {@link WindowRange#all}. Only has effect on the
     * {@link #mapColumn(Exp)} operation.
     *
     * @since 0.14
     */
    public Window range(WindowRange range) {
        this.range = range;
        return this;
    }

    /**
     * Generates a DataFrame of the same height as the source DataFrame, with columns generated from the provided
     * aggregating expressions. Aggregating expressions are invoked once per each row, and are passed the range of rows
     * corresponding to the partitioning, sorting and range settings.
     *
     * @since 1.0.0-M22
     */
    public DataFrame select(Exp<?>... aggregators) {
        return new ColumnDataFrame(null,
                Index.ofDeduplicated(selectLabels(aggregators)),
                selectColumns(aggregators));
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #select(Exp[])}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public DataFrame agg(Exp<?>... aggregators) {
        return select(aggregators);
    }

    /**
     * Generates a DataFrame of the same height as the source DataFrame, combining columns generated from the provided
     * aggregating expressions with the original DataFrame columns. Aggregating expressions are invoked once per each
     * row, and are passed the range of rows corresponding to the partitioning, sorting and range settings.
     *
     * @since 1.0.0-M22
     */
    public DataFrame map(Exp<?>... aggregators) {
        String[] labels = selectLabels(aggregators);
        return ColumnSetMerger.merge(source, labels, selectColumns(aggregators));
    }

    /**
     * Applies an aggregating expression to each DataFrame row, passing it a window associated with the current row.
     *
     * @since 0.14
     * @deprecated use {@link #map(Exp[])} or {@link #select(Exp[])} instead.
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public <T> Series<T> mapColumn(Exp<T> windowAggregator) {
        return select(windowAggregator).getColumn(0);
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

    private String[] selectLabels(Exp<?>... aggregators) {
        return columnSetIndex != null
                ? columnSetIndex.getLabels()
                : Exps.labels(source, aggregators);
    }

    private Series<?>[] selectColumns(Exp<?>... aggregators) {
        return partitioner != null ? selectPartitioned(aggregators) : selectUnPartitioned(aggregators);
    }

    private Series<?>[] selectPartitioned(Exp<?>... aggregators) {
        GroupBy gb = sorter != null
                ? source.group(partitioner).sort(sorter)
                : source.group(partitioner);

        int h = gb.getSource().height();
        int aggW = aggregators.length;

        Object[][] data = new Object[aggW][h];

        for (int i = 0; i < aggW; i++) {
            data[i] = new Object[h];
        }

        // TODO: parallelize aggregation by group key and by aggregator...

        for (Object key : gb.getGroupKeys()) {

            Series<?>[] gAggs = RangeAggregator.of(gb.getGroup(key), resolveRange()).agg(aggregators);

            IntSeries gIndex = gb.getGroupIndex(key);
            int ih = gIndex.size();
            for (int i = 0; i < aggW; i++) {
                Series<?> gAgg = gAggs[i];
                for (int j = 0; j < ih; j++) {
                    data[i][gIndex.getInt(j)] = gAgg.get(j);
                }
            }
        }

        Series<?>[] columns = new Series[aggW];
        for (int i = 0; i < aggW; i++) {
            columns[i] = Series.of(data[i]);
        }

        return columns;
    }

    private Series<?>[] selectUnPartitioned(Exp<?>... aggregators) {
        DataFrame df = sorter != null
                ? source.rows(DataFrameSorter.sort(sorter, source.height())).select()
                : source;

        return RangeAggregator.of(df, resolveRange()).agg(aggregators);
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
