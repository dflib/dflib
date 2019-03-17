package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.map.RowToValueMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupBy {

    private Index ungroupedColumns;
    private Map<Object, DataFrame> groups;

    public GroupBy(Index ungroupedColumns, Map<Object, DataFrame> groups) {
        this.ungroupedColumns = ungroupedColumns;
        this.groups = groups;
    }

    public int size() {
        return groups.size();
    }

    public Collection<Object> getGroups() {
        return groups.keySet();
    }

    public DataFrame getGroup(Object key) {
        return groups.get(key);
    }

    public <V extends Comparable<? super V>> GroupBy sort(RowToValueMapper<V> sortKeyExtractor) {
        Map<Object, DataFrame> sorted = new LinkedHashMap<>((int) (groups.size() / 0.75));

        for (Map.Entry<Object, DataFrame> e : groups.entrySet()) {
            sorted.put(e.getKey(), e.getValue().sort(sortKeyExtractor));
        }

        return new GroupBy(ungroupedColumns, sorted);
    }

    public <V extends Comparable<? super V>> GroupBy sortByColumns(String... columns) {
        if (columns.length == 0) {
            return this;
        }

        Map<Object, DataFrame> sorted = new LinkedHashMap<>((int) (groups.size() / 0.75));

        for (Map.Entry<Object, DataFrame> e : groups.entrySet()) {
            sorted.put(e.getKey(), e.getValue().sortByColumns(columns));
        }

        return new GroupBy(ungroupedColumns, sorted);
    }

    public <V extends Comparable<? super V>> GroupBy sortByColumns(int... columns) {
        if (columns.length == 0) {
            return this;
        }

        Map<Object, DataFrame> sorted = new LinkedHashMap<>((int) (groups.size() / 0.75));

        for (Map.Entry<Object, DataFrame> e : groups.entrySet()) {
            sorted.put(e.getKey(), e.getValue().sortByColumns(columns));
        }

        return new GroupBy(ungroupedColumns, sorted);
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
        return agg(aggregator.aggregateIndex(ungroupedColumns), aggregator);
    }

    public DataFrame agg(Index index, Aggregator aggregator) {

        List<Object[]> result = new ArrayList<>(groups.size());
        for (DataFrame df : groups.values()) {
            result.add(aggregator.aggregate(df));
        }

        return DataFrame.fromListOfRows(index, result);
    }
}
