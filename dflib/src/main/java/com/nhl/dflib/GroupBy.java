package com.nhl.dflib;

import com.nhl.dflib.aggregate.DataFrameAggregation;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.series.EmptySeries;
import com.nhl.dflib.series.IntSequenceSeries;
import com.nhl.dflib.sort.IntComparator;
import com.nhl.dflib.sort.Comparators;
import com.nhl.dflib.sort.DataFrameSorter;
import com.nhl.dflib.window.DenseRanker;
import com.nhl.dflib.window.Ranker;
import com.nhl.dflib.window.RowNumberer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GroupBy {

    private final DataFrame ungrouped;
    private final Map<Object, IntSeries> groupsIndex;
    private Map<Object, DataFrame> resolvedGroups;
    private final IntComparator sorter;

    public GroupBy(DataFrame ungrouped, Map<Object, IntSeries> groupsIndex, IntComparator sorter) {
        this.ungrouped = ungrouped;
        this.groupsIndex = groupsIndex;
        this.sorter = sorter;
    }

    public int size() {
        return groupsIndex.size();
    }

    /**
     * @since 0.6
     */
    public Index getUngroupedColumnIndex() {
        return ungrouped.getColumnsIndex();
    }

    /**
     * Recombines groups back to a DataFrame, preserving the effects of the initial grouping, and per-group sorting,
     * truncation and other operations.
     *
     * @return a new DataFrame made from recombined groups.
     */
    public DataFrame toDataFrame() {
        IntSeries index = SeriesConcat.intConcat(groupsIndex.values());
        return ungrouped.selectRows(index);
    }

    public Collection<Object> getGroups() {
        return groupsIndex.keySet();
    }

    public boolean hasGroup(Object key) {
        return groupsIndex.containsKey(key);
    }

    public IntSeries getGroupIndex(Object key) {
        return groupsIndex.get(key);
    }

    public DataFrame getGroup(Object key) {
        if (resolvedGroups == null) {
            resolvedGroups = new ConcurrentHashMap<>();
        }

        // TODO: nulls will blow up on read... check for nulls and do something right here..
        return resolvedGroups.computeIfAbsent(key, this::resolveGroup);
    }

    /**
     * A "window" function that converts this grouping into a Series that provides row numbers of each row within their
     * group. The order of row numbers corresponds to the order of rows in the original DataFrame that was used to
     * build the grouping. This Series can be added back to the original DataFrame, providing it with a per-group
     * ranking column.
     *
     * @return a new Series object with row numbers of each row within their group. The overall order matches the order
     * of the original DataFrame that was used to build the grouping.
     * @deprecated since 0.8 in favor of {@link #rowNumber()}. The difference is that this method numbers rows starting
     * with "0", while {@link #rowNumber()} starts with "1", as all other implementations of window functions do.
     */
    @Deprecated
    public Series<Integer> rowNumbers() {
        if (groupsIndex.size() == 0) {
            return IntSeries.forInts();
        }

        IntSeries[] rowNumbers = new IntSeries[groupsIndex.size()];

        int i = 0;
        for (IntSeries s : groupsIndex.values()) {
            rowNumbers[i] = new IntSequenceSeries(0, s.size());
            i++;
        }

        IntSeries groupsIndexGlued = SeriesConcat.intConcat(groupsIndex.values());
        IntSeries rowNumbersGlued = SeriesConcat.intConcat(rowNumbers);

        return rowNumbersGlued.select(groupsIndexGlued.sortIndexInt());
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
            return IntSeries.forInts();
        }

        return RowNumberer.rowNumber(ungrouped, groupsIndex.values());
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
            return IntSeries.forInts();
        }

        if (sorter == null) {
            return Ranker.sameRank(ungrouped.height());
        }

        return new Ranker(sorter).rank(ungrouped, groupsIndex.values());
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
            return IntSeries.forInts();
        }

        if (sorter == null) {
            return Ranker.sameRank(ungrouped.height());
        }

        return new DenseRanker(sorter).rank(ungrouped, groupsIndex.values());
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
        int pos = ungrouped.getColumnsIndex().position(column);
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
        for (Object key : getGroups()) {
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
            trimmed.put(e.getKey(), e.getValue().headInt(len));
        }

        return new GroupBy(ungrouped, trimmed, sorter);
    }

    public GroupBy tail(int len) {

        if (len < 0) {
            // TODO: treat negative len as counting from the other end
            throw new IllegalArgumentException("Length must be non-negative: " + len);
        }

        Map<Object, IntSeries> trimmed = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            trimmed.put(e.getKey(), e.getValue().tailInt(len));
        }

        return new GroupBy(ungrouped, trimmed, sorter);
    }

    public <V extends Comparable<? super V>> GroupBy sort(RowToValueMapper<V> sortKeyExtractor) {
        return sort(Comparators.of(ungrouped, sortKeyExtractor));
    }

    /**
     * @since 0.11
     */
    public GroupBy sort(IntComparator sorter) {

        Objects.requireNonNull(sorter, "Null 'sorter'");

        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new DataFrameSorter(ungrouped, e.getValue()).sortedPositions(sorter);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted, sorter);
    }

    public GroupBy sort(String column, boolean ascending) {

        IntComparator sorter = Comparators.of(ungrouped.getColumn(column), ascending);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new DataFrameSorter(ungrouped, e.getValue()).sortedPositions(sorter);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted, sorter);
    }

    public GroupBy sort(int column, boolean ascending) {
        IntComparator sorter = Comparators.of(ungrouped.getColumn(column), ascending);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new DataFrameSorter(ungrouped, e.getValue()).sortedPositions(sorter);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted, sorter);
    }

    public GroupBy sort(String[] columns, boolean[] ascending) {
        if (columns.length == 0) {
            return this;
        }

        IntComparator sorter = Comparators.of(ungrouped, columns, ascending);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new DataFrameSorter(ungrouped, e.getValue()).sortedPositions(sorter);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted, sorter);
    }

    public GroupBy sort(int[] columns, boolean[] ascending) {
        if (columns.length == 0) {
            return this;
        }

        IntComparator sorter = Comparators.of(ungrouped, columns, ascending);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new DataFrameSorter(ungrouped, e.getValue()).sortedPositions(sorter);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted, sorter);
    }

    public DataFrame agg(Aggregator<?>... aggregators) {
        return DataFrameAggregation.aggGroupBy(this, aggregators);
    }

    protected DataFrame resolveGroup(Object key) {

        IntSeries index = groupsIndex.get(key);
        if (index == null) {
            return null;
        }

        int w = ungrouped.width();
        Series[] data = new Series[w];

        for (int j = 0; j < w; j++) {
            data[j] = ungrouped.getColumn(j).select(index);
        }

        return new ColumnDataFrame(ungrouped.getColumnsIndex(), data);
    }
}
