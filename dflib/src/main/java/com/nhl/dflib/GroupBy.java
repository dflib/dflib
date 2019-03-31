package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.concat.VConcat;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.seq.Sequences;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.HeadSeries;
import com.nhl.dflib.series.IndexedSeries;
import com.nhl.dflib.series.TailSeries;
import com.nhl.dflib.sort.IndexSorter;
import com.nhl.dflib.sort.Sorters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupBy {

    private static final Index TWO_COLUMN_INDEX = Index.forLabels("0", "1");

    private DataFrame ungrouped;
    private Map<Object, Series<Integer>> groupsIndex;
    private Map<Object, DataFrame> resolvedGroups;

    public GroupBy(DataFrame ungrouped, Map<Object, Series<Integer>> groupsIndex) {
        this.ungrouped = ungrouped;
        this.groupsIndex = groupsIndex;
    }

    public int size() {
        return groupsIndex.size();
    }

    /**
     * Recombines groups back to a DataFrame, preserving the effects of the initial grouping, and per-group sorting,
     * truncation and other operations.
     *
     * @return a new DataFrame made from recombined groups.
     */
    public DataFrame toDataFrame() {
        Series<Integer> index = SeriesConcat.concat(groupsIndex.values());
        return ungrouped.select(index);
    }

    public Collection<Object> getGroups() {
        return groupsIndex.keySet();
    }

    public boolean hasGroup(Object key) {
        return groupsIndex.containsKey(key);
    }

    public DataFrame getGroup(Object key) {
        if (resolvedGroups == null) {
            resolvedGroups = new ConcurrentHashMap<>();
        }

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

        DataFrame[] numberedIndex = new DataFrame[groupsIndex.size()];

        int i = 0;
        for (Series<Integer> s : groupsIndex.values()) {
            Series<Integer> numbersWithGroup = new ArraySeries<>(Sequences.numberSequence(s.size()));
            numberedIndex[i] = new ColumnDataFrame(TWO_COLUMN_INDEX, s, numbersWithGroup);
            i++;
        }

        return VConcat.concat(JoinType.inner, numberedIndex).sort(0, true).getColumn(1);
    }

    public GroupBy head(int len) {

        if (len < 0) {
            throw new IllegalArgumentException("Length must be non-negative: " + len);
        }

        Map<Object, Series<Integer>> trimmed = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, Series<Integer>> e : groupsIndex.entrySet()) {
            Series<Integer> maybeTrimmedGroup = HeadSeries.forSeries(e.getValue(), len);
            trimmed.put(e.getKey(), maybeTrimmedGroup);
        }

        return new GroupBy(ungrouped, trimmed);
    }

    public GroupBy tail(int len) {

        if (len < 0) {
            throw new IllegalArgumentException("Length must be non-negative: " + len);
        }

        Map<Object, Series<Integer>> trimmed = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, Series<Integer>> e : groupsIndex.entrySet()) {
            Series<Integer> maybeTrimmedGroup = TailSeries.forSeries(e.getValue(), len);
            trimmed.put(e.getKey(), maybeTrimmedGroup);
        }

        return new GroupBy(ungrouped, trimmed);
    }

    public <V extends Comparable<? super V>> GroupBy sort(RowToValueMapper<V> sortKeyExtractor) {

        Comparator<RowProxy> comparator = Sorters.sorter(sortKeyExtractor);
        Map<Object, Series<Integer>> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, Series<Integer>> e : groupsIndex.entrySet()) {
            Series<Integer> sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public GroupBy sort(String column, boolean ascending) {

        Comparator<RowProxy> comparator = Sorters.sorter(ungrouped.getColumnsIndex(), column, ascending);
        Map<Object, Series<Integer>> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, Series<Integer>> e : groupsIndex.entrySet()) {
            Series<Integer> sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public GroupBy sort(int column, boolean ascending) {
        Comparator<RowProxy> comparator = Sorters.sorter(ungrouped.getColumnsIndex(), column, ascending);
        Map<Object, Series<Integer>> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, Series<Integer>> e : groupsIndex.entrySet()) {
            Series<Integer> sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public GroupBy sort(String[] columns, boolean[] ascending) {
        if (columns.length == 0) {
            return this;
        }

        Comparator<RowProxy> comparator = Sorters.sorter(ungrouped.getColumnsIndex(), columns, ascending);
        Map<Object, Series<Integer>> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, Series<Integer>> e : groupsIndex.entrySet()) {
            Series<Integer> sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public GroupBy sort(int[] columns, boolean[] ascending) {
        if (columns.length == 0) {
            return this;
        }

        Comparator<RowProxy> comparator = Sorters.sorter(ungrouped.getColumnsIndex(), columns, ascending);
        Map<Object, Series<Integer>> sorted = new LinkedHashMap<>((int) (groupsIndex.size() / 0.75));

        for (Map.Entry<Object, Series<Integer>> e : groupsIndex.entrySet()) {
            Series<Integer> sortedGroup = new IndexSorter(ungrouped, e.getValue()).sortIndex(comparator);
            sorted.put(e.getKey(), sortedGroup);
        }

        return new GroupBy(ungrouped, sorted);
    }

    public DataFrame agg(ColumnAggregator... aggregators) {
        return agg(Aggregator.forColumns(aggregators));
    }

    public DataFrame agg(Index index, ColumnAggregator... aggregators) {

        if (index.size() != aggregators.length) {
            throw new IllegalArgumentException("Index width does not match the number of aggregators. "
                    + index.size() + " vs. " + aggregators.length);
        }

        return agg(index, Aggregator.forColumns(aggregators));
    }

    public DataFrame agg(Aggregator aggregator) {
        return agg(aggregator.aggregateIndex(ungrouped.getColumnsIndex()), aggregator);
    }

    public DataFrame agg(Index index, Aggregator aggregator) {

        List<Object[]> result = new ArrayList<>(size());
        for (Object key : getGroups()) {
            result.add(aggregator.aggregate(getGroup(key)));
        }

        return DataFrame.forListOfRows(index, result);
    }

    protected DataFrame resolveGroup(Object key) {

        Series<Integer> index = groupsIndex.get(key);
        if (index == null) {
            throw new IllegalArgumentException("Group '" + key + "' is not present in GroupBy");
        }

        int w = ungrouped.width();
        Series[] data = new Series[w];

        for (int j = 0; j < w; j++) {
            data[j] = new IndexedSeries(ungrouped.getColumn(j), index);
        }

        return new ColumnDataFrame(ungrouped.getColumnsIndex(), data);
    }
}
