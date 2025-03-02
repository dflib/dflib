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
     */
    public DataFrame getSource() {
        return source;
    }

    /**
     * Specifies the columns of the aggregation or select result.
     */
    public Window cols(Predicate<String> colsPredicate) {
        Index srcIndex = source.getColumnsIndex();
        this.columnSetIndex = FixedColumnSetIndex.of(srcIndex, srcIndex.positions(colsPredicate));
        return this;
    }

    /**
     * Specifies the columns of the aggregation or select result.
     */
    public Window cols(String... cols) {
        this.columnSetIndex = FixedColumnSetIndex.of(cols);
        return this;
    }

    /**
     * Specifies the columns for the select result.
     */
    public Window cols(int... cols) {
        Index srcIndex = source.getColumnsIndex();
        this.columnSetIndex = FixedColumnSetIndex.of(srcIndex, cols);
        return this;
    }

    public Window colsExcept(String... cols) {
        return cols(source.getColumnsIndex().positionsExcept(cols));
    }

    public Window colsExcept(int... cols) {
        return cols(source.getColumnsIndex().positionsExcept(cols));
    }

    public Window colsExcept(Predicate<String> colsPredicate) {
        return cols(colsPredicate.negate());
    }

    public Window partition(Hasher partitioner) {
        this.partitioner = Objects.requireNonNull(partitioner);
        return this;
    }

    /**
     * @deprecated in favor of {@link #partition(String...)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Window partitioned(String... columns) {
        return partition(columns);
    }

    /**
     * @deprecated in favor of {@link #partition(int...)} 
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Window partitioned(int... columns) {
        return partition(columns);
    }

    /**
     * @deprecated in favor of {@link #partition(Hasher)} 
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Window partitioned(Hasher partitioner) {
        return partition(partitioner);
    }

    /**
     * @since 2.0.0
     */
    public Window partition(String... columns) {

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
     * @since 2.0.0
     */
    public Window partition(int... columns) {
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
     * @deprecated in favor of {@link #sort(Sorter...)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Window sorted(Sorter... sorters) {
        return sort(sorters);
    }

    /**
     * @deprecated in favor of {@link #sort(String, boolean)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Window sorted(String column, boolean ascending) {
        return sort(column, ascending);
    }

    /**
     * @deprecated in favor of {@link #sort(int, boolean)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Window sorted(int column, boolean ascending) {
        return sort(column, ascending);
    }

    /**
     * @deprecated in favor of {@link #sort(String[], boolean[])}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Window sorted(String[] columns, boolean[] ascending) {
        return sort(columns, ascending);
    }

    /**
     * @deprecated in favor of {@link #sort(int[], boolean[])}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Window sorted(int[] columns, boolean[] ascending) {
        return sort(columns, ascending);
    }

    /**
     * @since 2.0.0
     */
    public Window sort(Sorter... sorters) {
        this.sorter = sorters.length == 0 ? null : Comparators.of(source, sorters);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public Window sort(String column, boolean ascending) {
        this.sorter = Comparators.of(source.getColumn(column), ascending);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public Window sort(int column, boolean ascending) {
        this.sorter = Comparators.of(source.getColumn(column), ascending);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public Window sort(String[] columns, boolean[] ascending) {
        this.sorter = Comparators.of(source, columns, ascending);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public Window sort(int[] columns, boolean[] ascending) {
        this.sorter = Comparators.of(source, columns, ascending);
        return this;
    }

    /**
     * Sets an explicit row range for the window. The default is {@link WindowRange#all}.
     */
    public Window range(WindowRange range) {
        this.range = range;
        return this;
    }

    /**
     * Generates a DataFrame of the same height as the source DataFrame, with columns generated from the provided
     * expressions. Expressions can be a mix of per-row and aggregating. They are invoked per each row, and are passed
     * the range of rows corresponding to the partitioning, sorting and range settings.
     */
    public DataFrame select(Exp<?>... exps) {
        return new ColumnDataFrame(null,
                Index.ofDeduplicated(selectLabels(exps)),
                selectColumns(exps));
    }

    /**
     * Generates a DataFrame of the same height as the source DataFrame, combining columns generated from the provided
     * expressions with the original DataFrame columns. Expressions can be a mix of per-row and aggregating. They are
     * invoked per each row, and are passed the range of rows corresponding to the partitioning, sorting and range
     * settings.
     */
    public DataFrame merge(Exp<?>... exps) {
        String[] labels = selectLabels(exps);
        return ColumnSetMerger.merge(source, labels, selectColumns(exps));
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

    public <T> Series<T> shift(String column, int offset) {
        return shift(column, offset, null);
    }

    public <T> Series<T> shift(String column, int offset, T filler) {
        int pos = source.getColumnsIndex().position(column);
        return shift(pos, offset, filler);
    }

    public <T> Series<T> shift(int column, int offset) {
        return shift(column, offset, null);
    }

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

            Series<?>[] gAggs = WindowColumnEvaluator.of(gb.getGroup(key), resolveRange()).eval(aggregators);

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

        return WindowColumnEvaluator.of(df, resolveRange()).eval(aggregators);
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
