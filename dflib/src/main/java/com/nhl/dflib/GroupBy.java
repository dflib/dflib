package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import com.nhl.dflib.aggregate.ColumnAggregator;

import java.util.ArrayList;
import java.util.Collection;
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

    public Collection<Object> groups() {
        return groups.keySet();
    }

    public DataFrame group(Object key) {
        return groups.get(key);
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

        return DataFrame.fromRowsList(index, result);
    }
}
