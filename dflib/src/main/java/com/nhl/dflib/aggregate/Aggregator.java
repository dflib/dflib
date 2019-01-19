package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.map.IndexMapper;

import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Defines aggregation operation on a DataFrame. Provides methods for composing an aggregator from individual columns
 * aggregators.
 */
public interface Aggregator {

    static Aggregator forColumns(ColumnAggregator... columnAggregators) {
        return columnAggregators.length == 1
                ? columnAggregators[0]
                : new MultiColumnAggregator(columnAggregators);
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     *
     * @param column
     * @return a new ColumnAggregator
     */
    static ColumnAggregator first(String column) {
        return of(column, Collectors.reducing(null, (l, r) -> l != null ? l : r));
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     *
     * @param column
     * @return a new ColumnAggregator
     */
    static ColumnAggregator first(int column) {
        return of(column, Collectors.reducing(null, (l, r) -> l != null ? l : r));
    }

    static ColumnAggregator count(String column) {
        return of(column, Collectors.counting());
    }

    static ColumnAggregator count(int column) {
        return of(column, Collectors.counting());
    }

    static ColumnAggregator average(String column) {
        return of(column, Collectors.averagingDouble((Number v) -> v.doubleValue()));
    }

    static ColumnAggregator average(int column) {
        return of(column, Collectors.averagingDouble((Number v) -> v.doubleValue()));
    }

    static ColumnAggregator median(String column) {
        return of(column, AggregatorFunctions.medianCollector());
    }

    static ColumnAggregator median(int column) {
        return of(column, AggregatorFunctions.medianCollector());
    }

    static ColumnAggregator sum(String column) {
        return of(column, Collectors.summingLong((Number v) -> v.longValue()));
    }

    static ColumnAggregator sum(int column) {
        return of(column, Collectors.summingLong((Number v) -> v.longValue()));
    }

    static ColumnAggregator sumDouble(String column) {
        return of(column, Collectors.summingDouble((Number v) -> v.doubleValue()));
    }

    static ColumnAggregator sumDouble(int column) {
        return of(column, Collectors.summingDouble((Number v) -> v.doubleValue()));
    }

    static ColumnAggregator concat(String column, String delimiter) {
        return new ColumnAggregator(
                IndexMapper.mapper(column),
                AggregatorFunctions.toString(column),
                Collectors.joining(delimiter));
    }

    static ColumnAggregator concat(int column, String delimiter) {
        return new ColumnAggregator(
                IndexMapper.mapper(column),
                AggregatorFunctions.toString(column),
                Collectors.joining(delimiter));
    }

    static ColumnAggregator concat(String column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator(
                IndexMapper.mapper(column),
                AggregatorFunctions.toString(column),
                Collectors.joining(delimiter, prefix, suffix));
    }

    static ColumnAggregator concat(int column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator(
                IndexMapper.mapper(column),
                AggregatorFunctions.toString(column),
                Collectors.joining(delimiter, prefix, suffix));
    }

    static ColumnAggregator list(String column) {
        return of(column, Collectors.toList());
    }

    static ColumnAggregator list(int column) {
        return of(column, Collectors.toList());
    }

    static ColumnAggregator set(String column) {
        return of(column, Collectors.toSet());
    }

    static ColumnAggregator set(int column) {
        return of(column, Collectors.toSet());
    }

    static ColumnAggregator of(String column, Collector<?, ?, ?> columnAggregator) {
        return new ColumnAggregator(
                IndexMapper.mapper(column),
                RowToValueMapper.columnReader(column),
                columnAggregator);
    }

    static ColumnAggregator of(int column, Collector<?, ?, ?> columnAggregator) {
        return new ColumnAggregator(
                IndexMapper.mapper(column),
                RowToValueMapper.columnReader(column),
                columnAggregator);
    }

    Index aggregateIndex(Index columns);

    Object[] aggregate(DataFrame df);
}
