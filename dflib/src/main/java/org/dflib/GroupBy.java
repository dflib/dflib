package org.dflib;

import org.dflib.agg.GroupByAggregator;
import org.dflib.concat.SeriesConcat;
import org.dflib.exp.Exps;
import org.dflib.series.EmptySeries;
import org.dflib.sort.GroupBySorter;
import org.dflib.sort.IntComparator;
import org.dflib.window.DenseRanker;
import org.dflib.window.Ranker;
import org.dflib.window.RowNumberer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class GroupBy {

    private final DataFrame source;
    private final Map<Object, IntSeries> groupsIndex;
    private final IntComparator sorter;
    private final ConcurrentMap<Object, DataFrame> groupsCache;

    private final boolean userColumns;
    private final UnaryOperator<Index> colSelector;

    public GroupBy(DataFrame source, Map<Object, IntSeries> groupsIndex, IntComparator sorter) {
        this(source, groupsIndex, sorter, false, null);
    }

    protected GroupBy(
            DataFrame source,
            Map<Object, IntSeries> groupsIndex,
            IntComparator sorter,
            boolean userColumns,
            UnaryOperator<Index> colSelector) {

        this.source = source;
        this.groupsIndex = groupsIndex;
        this.sorter = sorter;
        this.userColumns = userColumns;
        this.colSelector = colSelector;

        this.groupsCache = new ConcurrentHashMap<>();
    }

    /**
     * Returns the number of groups. For the underlying DataFrame size, use "getSource().height()".
     */
    public int size() {
        return groupsIndex.size();
    }

    /**
     * Returns the unchanged original DataFrame that was used in the grouping, that does not have GroupBy sorting,
     * trimming and other changes applied.
     *
     * @since 1.0.0-M21
     */
    public DataFrame getSource() {
        return source;
    }

    /**
     * @since 0.11
     * @deprecated in favor of {@link #getSource()}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public DataFrame getUngrouped() {
        return getSource();
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #getSource()} and then {@link DataFrame#getColumnsIndex()}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index getUngroupedColumnIndex() {
        return source.getColumnsIndex();
    }

    /**
     * Specifies the columns of the aggregation or select result.
     */
    public GroupBy cols(Predicate<String> colsPredicate) {
        return new GroupBy(source, groupsIndex, sorter, true, i -> i.selectLabels(colsPredicate));
    }

    /**
     * Specifies the columns of the aggregation or select result.
     */
    public GroupBy cols(String... cols) {
        return new GroupBy(source, groupsIndex, sorter, true, i -> Index.of(cols));
    }

    /**
     * Specifies the columns of the aggregation or select result.
     */
    public GroupBy cols(int... cols) {
        return new GroupBy(source, groupsIndex, sorter, true, i -> i.selectPositions(cols));
    }

    /**
     * @since 1.0.0-M21
     */
    public GroupBy colsExcept(String... cols) {
        return cols(source.getColumnsIndex().positionsExcept(cols));
    }

    /**
     * @since 1.0.0-M21
     */
    public GroupBy colsExcept(int... cols) {
        return cols(source.getColumnsIndex().positionsExcept(cols));
    }

    /**
     * @since 1.0.0-M21
     */
    public GroupBy colsExcept(Predicate<String> colsPredicate) {
        return cols(colsPredicate.negate());
    }

    /**
     * @deprecated in favor of {@link #select()}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public DataFrame toDataFrame() {
        return select();
    }

    /**
     * @since 1.0.0-M21
     */
    public Set<Object> getGroupKeys() {
        return groupsIndex.keySet();
    }

    /**
     * @deprecated in favor of {@link #getGroupKeys()}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Set<Object> getGroups() {
        return getGroupKeys();
    }

    public boolean hasGroup(Object key) {
        return groupsIndex.containsKey(key);
    }

    public IntSeries getGroupIndex(Object key) {
        return groupsIndex.get(key);
    }

    public DataFrame getGroup(Object key) {
        // TODO: nulls will blow up on read... check for nulls and do something right here..
        return groupsCache.computeIfAbsent(key, this::resolveGroup);
    }

    /**
     * A window function that returns an IntSeries for this grouping that contains a row number of each row within its
     * group. The order of row numbers corresponds to the order of rows in the original DataFrame used to
     * build the grouping. So the returned Series can be added back to the original DataFrame, providing it with a
     * per-group ranking column.
     *
     * @return a new Series object with ranking numbers of each row within its group. The order matches the order of
     * the original DataFrame that was used to build the grouping.
     * @since 0.8
     */
    public IntSeries rowNumber() {

        if (groupsIndex.size() == 0) {
            return Series.ofInt();
        }

        return RowNumberer.rowNumber(source, groupsIndex.values());
    }

    /**
     * A window function that returns an IntSeries for this grouping that contains a "rank" of each row within its
     * group. The order of row numbers corresponds to the order of rows in the original DataFrame used to
     * build the grouping. So the returned Series can be added back to the original DataFrame, providing it with a
     * per-group ranking column.
     *
     * <p>Ranking values are controlled by the sort Comparator passed to this GroupBy when it was created. Default
     * ranking (when there's no defined ordering) is "1" for all rows.</p>
     *
     * @return a new Series object with rankings of each row within its group. The order matches the order of the
     * original DataFrame that was used to build the grouping.
     * @since 0.8
     */
    public IntSeries rank() {

        if (groupsIndex.size() == 0) {
            return Series.ofInt();
        }

        if (sorter == null) {
            // no sort order means each row is equivalent from the ranking perspective, so return a Series of 1's
            return Ranker.sameRank(source.height());
        }

        return new Ranker(sorter).rank(source, groupsIndex.values());
    }

    /**
     * A window function that returns an IntSeries for this grouping that contains a "dense rank" of each row within its
     * group. This operation is similar to {@link #rank()}, except it leaves no gaps in the ranking sequence due to
     * duplicate entries.
     *
     * @return a new Series object with rankings of each row within its group. The order matches the order of the
     * original DataFrame that was used to build the grouping.
     * @since 0.8
     */
    public IntSeries denseRank() {

        if (groupsIndex.size() == 0) {
            return Series.ofInt();
        }

        if (sorter == null) {
            // no sort order means each row is equivalent from the ranking perspective, so return a Series of 1's
            return Ranker.sameRank(source.height());
        }

        return new DenseRanker(sorter).rank(source, groupsIndex.values());
    }

    /**
     * A window function that does a {@link Series#shift(int, Object)} operation on the named column in each group.
     * Produces a Series with the same size as the original DataFrame height, with values shifted forward or backwards
     * within each group depending on the sign of the offset parameter. Gaps produced by the shift are filled with the
     * provided filler value.
     *
     * @since 0.9
     */
    public <T> Series<T> shift(String column, int offset, T filler) {
        int pos = source.getColumnsIndex().position(column);
        return shift(pos, offset, filler);
    }

    /**
     * A window function that does a {@link Series#shift(int, Object)} operation in each group on the column with the
     * specified number. Produces a Series with the same size as the original DataFrame height, with values shifted
     * forward or backwards within each group depending on the sign of the offset parameter. Gaps produced by the shift
     * are filled with the provided filler value.
     *
     * @since 0.9
     */
    public <T> Series<T> shift(int column, int offset, T filler) {
        if (groupsIndex.size() == 0) {
            return new EmptySeries<>();
        }

        Series[] shifted = new Series[groupsIndex.size()];

        int i = 0;
        for (Object key : getGroupKeys()) {
            DataFrame group = getGroup(key);
            shifted[i++] = group.getColumn(column).shift(offset, filler);
        }

        IntSeries groupsIndexAll = SeriesConcat.intConcat(groupsIndex.values());
        Series<T> shiftedAll = SeriesConcat.concat(shifted);

        return shiftedAll.select(groupsIndexAll.sortIndexInt());
    }

    public GroupBy head(int len) {

        if (len < 0) {
            // TODO: treat negative len as counting from the other end
            throw new IllegalArgumentException("Length must be non-negative: " + len);
        }

        Map<Object, IntSeries> trimmed = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            trimmed.put(e.getKey(), e.getValue().head(len));
        }

        return new GroupBy(source, trimmed, sorter);
    }

    public GroupBy tail(int len) {

        if (len < 0) {
            // TODO: treat negative len as counting from the other end
            throw new IllegalArgumentException("Length must be non-negative: " + len);
        }

        Map<Object, IntSeries> trimmed = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            trimmed.put(e.getKey(), e.getValue().tail(len));
        }

        return new GroupBy(source, trimmed, sorter);
    }

    /**
     * @since 0.11
     */
    public GroupBy sort(Sorter... sorters) {
        return new GroupBySorter(this).sort(sorters);
    }

    /**
     * @since 0.11
     */
    public GroupBy sort(IntComparator sorter) {
        return new GroupBySorter(this).sort(sorter);
    }

    public GroupBy sort(String column, boolean ascending) {
        return new GroupBySorter(this).sort(column, ascending);
    }

    public GroupBy sort(int column, boolean ascending) {
        return new GroupBySorter(this).sort(column, ascending);
    }

    public GroupBy sort(String[] columns, boolean[] ascending) {
        return new GroupBySorter(this).sort(columns, ascending);
    }

    public GroupBy sort(int[] columns, boolean[] ascending) {
        return new GroupBySorter(this).sort(columns, ascending);
    }

    /**
     * Recombines groups back to a DataFrame, preserving the effects of the initial grouping, and per-group sorting,
     * truncation and other operations.
     *
     * @return a new DataFrame made from recombined groups.
     * @since 1.0.0-M21
     */
    public DataFrame select() {
        IntSeries index = SeriesConcat.intConcat(groupsIndex.values());

        return userColumns
                ? source.rows(index).cols(colSelector.apply(source.getColumnsIndex())).select()
                : source.rows(index).select();
    }

    public DataFrame agg(Exp<?>... aggregators) {

        Index index = userColumns
                ? colSelector.apply(source.getColumnsIndex())
                : Exps.index(source, aggregators);

        if (userColumns) {
            int w = aggregators.length;
            if (w != index.size()) {
                throw new IllegalArgumentException(
                        "Can't perform 'agg': Exp[] size is different from the ColumnSet size: " + w + " vs. " + index.size());
            }
        }

        return new ColumnDataFrame(null,
                index,
                GroupByAggregator.agg(this, aggregators));
    }

    protected DataFrame resolveGroup(Object key) {

        IntSeries index = groupsIndex.get(key);
        if (index == null) {
            return null;
        }

        int w = source.width();
        Series[] data = new Series[w];

        for (int j = 0; j < w; j++) {
            data[j] = source.getColumn(j).select(index);
        }

        return new ColumnDataFrame(null, source.getColumnsIndex(), data);
    }
}
