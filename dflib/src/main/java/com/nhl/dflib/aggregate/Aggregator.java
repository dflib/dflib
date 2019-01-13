package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.map.DataRowToValueMapper;

import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Defines aggregation operation on a DataFrame. Provides methods for composing an aggregator from individual columns
 * aggregators.
 */
@FunctionalInterface
public interface Aggregator {

    static Aggregator forColumns(ColumnAggregator... columnAggregators) {
        return columnAggregators.length == 1
                ? columnAggregators[0]
                : new MultiColumnAggregator(columnAggregators);
    }

    static ColumnAggregator count(String column) {
        return of(column, Collectors.counting());
    }

    static ColumnAggregator count(int column) {
        return of(column, Collectors.counting());
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
        return new ColumnAggregator(AggregatorFunctions.toString(column), Collectors.joining(delimiter));
    }

    static ColumnAggregator concat(int column, String delimiter) {
        return new ColumnAggregator(AggregatorFunctions.toString(column), Collectors.joining(delimiter));
    }

    static ColumnAggregator concat(String column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator(
                AggregatorFunctions.toString(column),
                Collectors.joining(delimiter, prefix, suffix));
    }

    static ColumnAggregator concat(int column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator(
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
        return new ColumnAggregator(DataRowToValueMapper.columnReader(column), columnAggregator);
    }

    static ColumnAggregator of(int column, Collector<?, ?, ?> columnAggregator) {
        return new ColumnAggregator(DataRowToValueMapper.columnReader(column), columnAggregator);
    }

    Object[] aggregate(DataFrame df);
}
