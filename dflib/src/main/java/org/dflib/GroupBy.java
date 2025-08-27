package org.dflib;

import org.dflib.agg.GroupByAggregator;
import org.dflib.concat.SeriesConcat;
import org.dflib.concat.VConcat;
import org.dflib.exp.Exps;
import org.dflib.series.EmptySeries;
import org.dflib.slice.FixedColumnSetIndex;
import org.dflib.sort.IntComparator;
import org.dflib.window.DenseRanker;
import org.dflib.window.Ranker;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

public class GroupBy {

    private final DataFrame source;
    private final Map<Object, IntSeries> groupsIndex;
    private final IntComparator sorter;
    private final ConcurrentMap<Object, DataFrame> groupsCache;
    private final FixedColumnSetIndex columnSetIndex;

    public GroupBy(DataFrame source, Map<Object, IntSeries> groupsIndex, IntComparator sorter) {
        this(source, groupsIndex, sorter, null);
    }

    protected GroupBy(
            DataFrame source,
            Map<Object, IntSeries> groupsIndex,
            IntComparator sorter,
            FixedColumnSetIndex columnSetIndex) {

        this.source = source;
        this.groupsIndex = groupsIndex;
        this.sorter = sorter;
        this.columnSetIndex = columnSetIndex;

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
     */
    public DataFrame getSource() {
        return source;
    }

    /**
     * Specifies the columns of the aggregation or select result.
     */
    public GroupBy cols(Predicate<String> colsPredicate) {
        Index srcIndex = source.getColumnsIndex();
        return new GroupBy(source, groupsIndex, sorter, FixedColumnSetIndex.of(srcIndex, srcIndex.positions(colsPredicate)));
    }

    /**
     * Specifies the columns of the aggregation or select result.
     */
    public GroupBy cols(String... cols) {
        return new GroupBy(source, groupsIndex, sorter, FixedColumnSetIndex.of(cols));
    }

    /**
     * Specifies the columns of the aggregation or select result.
     */
    public GroupBy cols(int... cols) {
        Index srcIndex = source.getColumnsIndex();
        return new GroupBy(source, groupsIndex, sorter, FixedColumnSetIndex.of(srcIndex, cols));
    }

    public GroupBy colsExcept(String... cols) {
        return cols(source.getColumnsIndex().positionsExcept(cols));
    }

    public GroupBy colsExcept(int... cols) {
        return cols(source.getColumnsIndex().positionsExcept(cols));
    }

    public GroupBy colsExcept(Predicate<String> colsPredicate) {
        return cols(colsPredicate.negate());
    }

    public Set<Object> getGroupKeys() {
        return groupsIndex.keySet();
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
     */
    public IntSeries rank() {

        if (groupsIndex.isEmpty()) {
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
     */
    public IntSeries denseRank() {

        if (groupsIndex.isEmpty()) {
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
     */
    public <T> Series<T> shift(int column, int offset, T filler) {
        if (groupsIndex.isEmpty()) {
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

    /**
     * Returns a GroupBy object that will only use the first <code>len</code> elements in each group. If a group is
     * shorter than the requested length, then the entire group is used. If <code>len</code> is negative, instead of
     * using the leading elements, they are skipped, and the rest of the group is used.
     */
    public GroupBy head(int len) {

        if (len == 0) {
            return this;
        }

        Map<Object, IntSeries> trimmed = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            trimmed.put(e.getKey(), e.getValue().head(len));
        }

        return new GroupBy(source, trimmed, sorter);
    }

    /**
     * Returns a GroupBy object that will only use the last <code>len</code> elements in each group. If a group is
     * shorter than the requested length, then the entire group is used. If <code>len</code> is negative, instead of
     * using the trailing elements, they are skipped, and the rest of the group is used.
     */
    public GroupBy tail(int len) {

        if (len == 0) {
            return this;
        }

        Map<Object, IntSeries> trimmed = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            trimmed.put(e.getKey(), e.getValue().tail(len));
        }

        return new GroupBy(source, trimmed, sorter);
    }

    /**
     * A noop sort operation. Useless on its own, and primarily exists to disambiguate {@link #sort(Sorter...)} and
     * {@link #sort(String...)} for no-arg sort call.
     *
     * @since 2.0.0
     */
    public GroupBy sort() {
        return this;
    }

    /**
     * @since 2.0.0
     */
    public GroupBy sort(String sortExps, Object... params) {
        return sort(Sorter.parseSorterArray(sortExps, params));
    }

    public GroupBy sort(Sorter... sorters) {
        return sorters.length == 0 ? this : sort(IntComparator.of(source, sorters));
    }

    public GroupBy sort(IntComparator sorter) {
        Objects.requireNonNull(sorter, "Null 'sorter'");

        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (size() / 0.75));

        for (Object groupKey : getGroupKeys()) {
            IntSeries groupIndex = getGroupIndex(groupKey);
            IntSeries sortedGroup = groupIndex.sortInt(sorter);
            sorted.put(groupKey, sortedGroup);
        }

        return new GroupBy(source, sorted, sorter);
    }

    public GroupBy sort(String column, boolean ascending) {
        return sort(IntComparator.of(source.getColumn(column), ascending));
    }

    public GroupBy sort(int column, boolean ascending) {
        return sort(IntComparator.of(source.getColumn(column), ascending));
    }

    public GroupBy sort(String[] columns, boolean[] ascending) {
        return sort(IntComparator.of(source, columns, ascending));
    }

    public GroupBy sort(int[] columns, boolean[] ascending) {
        return sort(IntComparator.of(source, columns, ascending));
    }

    /**
     * Recombines groups back to a DataFrame, preserving the effects of the initial grouping, and per-group sorting,
     * truncation and other operations.
     *
     * @return a new DataFrame made from recombined groups.
     */
    public DataFrame select() {
        IntSeries index = SeriesConcat.intConcat(groupsIndex.values());

        return columnSetIndex != null
                ? source.rows(index).cols(columnSetIndex.getIndex()).select()
                : source.rows(index).select();
    }

    /**
     * Parses the provided String into expressions and then recombines groups back to a single DataFrame with columns
     * generated by those expressions. Expressions are applied per-group, which is especially useful for window
     * functions like {@link Exp#rowNum()}. The operation preserves the effects of the grouping, per-group sorting,
     * truncation and other operations.
     *
     * @return a new DataFrame made from recombined groups after applying provided expressions
     * @since 2.0.0
     */
    public DataFrame select(String exps, Object... params) {
        return select(Exp.parseExpArray(exps, params));
    }

    /**
     * Recombines groups back to a single DataFrame with columns generated by the specified expressions. Expressions are
     * applied per-group, which is especially useful for window functions like {@link Exp#rowNum()}. The operation
     * preserves the effects of the grouping, per-group sorting, truncation and other operations.
     *
     * @return a new DataFrame made from recombined groups after applying provided expressions
     */
    public DataFrame select(Exp<?>... exps) {

        int len = groupsIndex.size();
        DataFrame[] dfs = new DataFrame[len];
        int i = 0;

        if (columnSetIndex != null) {
            Index customIndex = columnSetIndex.getIndex();
            for (IntSeries gi : groupsIndex.values()) {
                dfs[i++] = source.rows(gi).cols(customIndex).select(exps);
            }
        } else {
            for (IntSeries gi : groupsIndex.values()) {
                dfs[i++] = source.rows(gi).cols().select(exps);
            }
        }

        return VConcat.concat(JoinType.inner, dfs);
    }

    /**
     * Parses the provided String into expressions and recombines groups back to a DataFrame that contains columns
     * from the source DataFrame and added / replaced columns produced by those expressions. Expressions are applied
     * per-group, which is especially useful for window functions like {@link Exp#rowNum()}. The operation preserves
     * the effects of the grouping, per-group sorting, truncation and other operations.
     *
     * @return a new DataFrame made from recombined groups after applying provided expressions
     * @since 2.0.0
     */
    public DataFrame merge(String exps, Object... params) {
        return merge(Exp.parseExpArray(exps, params));
    }

    /**
     * Recombines groups back to a DataFrame that contains columns from the source DataFrame and added / replaced columns
     * produced by the specified expressions. Expressions are applied per-group, which is especially useful for
     * window functions like {@link Exp#rowNum()}. The operation preserves the effects of the grouping, per-group sorting,
     * truncation and other operations.
     *
     * @return a new DataFrame made from recombined groups after applying provided expressions
     */
    public DataFrame merge(Exp<?>... exps) {

        int len = groupsIndex.size();
        DataFrame[] dfs = new DataFrame[len];
        int i = 0;

        if (columnSetIndex != null) {
            Index customIndex = columnSetIndex.getIndex();

            for (IntSeries gi : groupsIndex.values()) {
                // must call "select()" before applying "map()", otherwise "map()" is applied to the row set, not columns
                dfs[i++] = source.rows(gi).select().cols(customIndex).merge(exps);
            }
        } else {
            for (IntSeries gi : groupsIndex.values()) {
                // must call "select()" before applying "map()", otherwise "map()" is applied to the row set, not columns
                dfs[i++] = source.rows(gi).select().cols().merge(exps);
            }
        }


        return VConcat.concat(JoinType.inner, dfs);
    }

    /**
     * @since 2.0.0
     */
    public DataFrame agg(String aggregatingExps, Object... params) {
        return agg(Exp.parseExpArray(aggregatingExps, params));
    }

    public DataFrame agg(Exp<?>... aggregatingExps) {

        Index index;

        if (columnSetIndex != null) {
            index = columnSetIndex.getIndex();

            int w = aggregatingExps.length;
            if (w != index.size()) {
                throw new IllegalArgumentException(
                        "Can't perform 'agg': Exp[] size is different from the ColumnSet size: " + w + " vs. " + index.size());
            }
        } else {
            index = Exps.index(source, aggregatingExps);
        }

        return new ColumnDataFrame(null,
                index,
                GroupByAggregator.agg(this, aggregatingExps));
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
