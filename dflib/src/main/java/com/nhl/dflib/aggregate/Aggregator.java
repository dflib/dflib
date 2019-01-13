package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.map.DataRowToValueMapper;

import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Defines aggregation operation on a DataFrame. Provides methods for composing an aggregator from individual columns
 * aggregators.
 */
public interface Aggregator {

    static Aggregator count(String column) {
        return of(column, Collectors.counting());
    }

    static Aggregator count(int column) {
        return of(column, Collectors.counting());
    }

    static Aggregator sum(String column) {
        return of(column, Collectors.summingLong((Number v) -> v.longValue()));
    }

    static Aggregator sum(int column) {
        return of(column, Collectors.summingLong((Number v) -> v.longValue()));
    }

    static Aggregator sumDouble(String column) {
        return of(column, Collectors.summingDouble((Number v) -> v.doubleValue()));
    }

    static Aggregator sumDouble(int column) {
        return of(column, Collectors.summingDouble((Number v) -> v.doubleValue()));
    }

    static Aggregator concat(String column, String delimiter) {
        return new SingleColumnAggregator(AggregatorFunctions.toString(column), Collectors.joining(delimiter));
    }

    static Aggregator concat(int column, String delimiter) {
        return new SingleColumnAggregator(AggregatorFunctions.toString(column), Collectors.joining(delimiter));
    }

    static Aggregator concat(String column, String delimiter, String prefix, String suffix) {
        return new SingleColumnAggregator(
                AggregatorFunctions.toString(column),
                Collectors.joining(delimiter, prefix, suffix));
    }

    static Aggregator concat(int column, String delimiter, String prefix, String suffix) {
        return new SingleColumnAggregator(
                AggregatorFunctions.toString(column),
                Collectors.joining(delimiter, prefix, suffix));
    }

    static Aggregator list(String column) {
        return of(column, Collectors.toList());
    }

    static Aggregator list(int column) {
        return of(column, Collectors.toList());
    }

    static Aggregator set(String column) {
        return of(column, Collectors.toSet());
    }

    static Aggregator set(int column) {
        return of(column, Collectors.toSet());
    }

    static Aggregator of(String column, Collector<?, ?, ?> columnAggregator) {
        return new SingleColumnAggregator(DataRowToValueMapper.columnReader(column), columnAggregator);
    }

    static Aggregator of(int column, Collector<?, ?, ?> columnAggregator) {
        return new SingleColumnAggregator(DataRowToValueMapper.columnReader(column), columnAggregator);
    }

    default Aggregator and(String column, Collector<?, ?, ?> columnAggregator) {
        return and(DataRowToValueMapper.columnReader(column), columnAggregator);
    }

    default Aggregator and(int column, Collector<?, ?, ?> columnAggregator) {
        return and(DataRowToValueMapper.columnReader(column), columnAggregator);
    }

    default Aggregator andCount(String column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.counting());
    }

    default Aggregator andCount(int column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.counting());
    }

    default Aggregator andSum(String column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.summingLong((Number v) -> v.longValue()));
    }

    default Aggregator andSum(int column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.summingLong((Number v) -> v.longValue()));
    }

    default Aggregator andSumDouble(String column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.summingDouble((Number v) -> v.doubleValue()));
    }

    default Aggregator andSumDouble(int column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.summingDouble((Number v) -> v.doubleValue()));
    }

    default Aggregator andConcat(String column, String delimiter) {
        return and(AggregatorFunctions.toString(column), Collectors.joining(delimiter));
    }

    default Aggregator andConcat(int column, String delimiter) {
        return and(AggregatorFunctions.toString(column), Collectors.joining(delimiter));
    }

    default Aggregator andConcat(String column, String delimiter, String prefix, String suffix) {
        return and(AggregatorFunctions.toString(column), Collectors.joining(delimiter, prefix, suffix));
    }

    default Aggregator andConcat(int column, String delimiter, String prefix, String suffix) {
        return and(AggregatorFunctions.toString(column), Collectors.joining(delimiter, prefix, suffix));
    }

    default Aggregator andList(String column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.toList());
    }

    default Aggregator andList(int column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.toList());
    }

    default Aggregator andSet(String column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.toSet());
    }

    default Aggregator andSet(int column) {
        return and(DataRowToValueMapper.columnReader(column), Collectors.toSet());
    }

    Aggregator and(DataRowToValueMapper columnReader, Collector<?, ?, ?> columnAggregator);

    Object[] aggregate(DataFrame df);
}
