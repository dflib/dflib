package com.nhl.dflib;

import com.nhl.dflib.aggregate.DataFrameAggregation;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.concat.VConcat;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.IntSequenceSeries;
import com.nhl.dflib.sort.IndexSorter;
import com.nhl.dflib.sort.Sorters;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupBy {

    private static final Index TWO_COLUMN_INDEX = Index.forLabels("0", "1");

    private DataFrame ungrouped;
    private Map<Object, IntSeries> groupsIndex;
    private Map<Object, DataFrame> resolvedGroups;

    public GroupBy(DataFrame ungrouped, Map<Object, IntSeries> groupsIndex) {
        this.ungrouped = ungrouped;
        this.groupsIndex = groupsIndex;
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
     */
    public Series<Integer> rowNumbers() {

        if(groupsIndex.size() == 0) {
            return IntSeries.forInts();
        }

        DataFrame[] numberedIndex = new DataFrame[groupsIndex.size()];

        int i = 0;
        for (IntSeries s : groupsIndex.values()) {
            IntSeries numbersWithGroup = new IntSequenceSeries(0, s.size());
            numberedIndex[i] = new ColumnDataFrame(TWO_COLUMN_INDEX, s, numbersWithGroup);
            i++;
        }

        return VConcat.concat(JoinType.inner, numberedIndex).sort(0, true).getColumn(1);
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

        return new GroupBy(ungrouped, trimmed);
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

        return new GroupBy(ungrouped, trimmed);
    }

    public <V extends Comparable<? super V>> GroupBy sort(RowToValueMapper<V> sortKeyExtractor) {

        Comparator<RowProxy> comparator = Sorters.sorter(sortKeyExtractor);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public GroupBy sort(String column, boolean ascending) {

        Comparator<RowProxy> comparator = Sorters.sorter(ungrouped.getColumnsIndex(), column, ascending);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public GroupBy sort(int column, boolean ascending) {
        Comparator<RowProxy> comparator = Sorters.sorter(ungrouped.getColumnsIndex(), column, ascending);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public GroupBy sort(String[] columns, boolean[] ascending) {
        if (columns.length == 0) {
            return this;
        }

        Comparator<RowProxy> comparator = Sorters.sorter(ungrouped.getColumnsIndex(), columns, ascending);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public GroupBy sort(int[] columns, boolean[] ascending) {
        if (columns.length == 0) {
            return this;
        }

        Comparator<RowProxy> comparator = Sorters.sorter(ungrouped.getColumnsIndex(), columns, ascending);
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, IntSeries> e : groupsIndex.entrySet()) {
            IntSeries sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
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
