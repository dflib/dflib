package com.nhl.dflib;

import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.aggregate.MultiColumnAggregator;
import com.nhl.dflib.aggregate.SingleColumnAggregator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

/**
 * Defines aggregation operation on a DataFrame. Provides methods for composing an aggregator from individual columns
 * aggregators.
 */
public interface Aggregator {

    static Aggregator forColumns(ColumnAggregator... columnAggregators) {
        return columnAggregators.length == 1
                ? new SingleColumnAggregator(columnAggregators[0])
                : new MultiColumnAggregator(columnAggregators);
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     *
     * @param column
     * @return a new ColumnAggregator
     */
    static ColumnAggregator<?, ?> first(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.first(), index -> index.position(column),
                index -> column
        );
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     *
     * @param column
     * @return a new ColumnAggregator
     */
    static <S> ColumnAggregator<S, S> first(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.first(), index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #countLong(String)}
     */
    @Deprecated
    static ColumnAggregator count(String column) {
        return countLong(column);
    }

    /**
     * Creates an aggregator to calculate a sum of the specified column.
     *
     * @param column
     * @return a new ColumnAggregator
     * @since 0.6
     */
    static <S> ColumnAggregator<S, Long> countLong(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.countLong(), index -> index.position(column),
                index -> column
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #countLong(int)}
     */
    @Deprecated
    static ColumnAggregator count(int column) {
        return countLong(column);
    }

    /**
     * Creates an aggregator to calculate a sum of the specified column.
     *
     * @param column
     * @return a new ColumnAggregator
     * @since 0.6
     */
    static <S> ColumnAggregator<S, Long> countLong(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.countLong(), index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #averageDouble(String)}
     */
    @Deprecated
    static ColumnAggregator average(String column) {
        return averageDouble(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #averageDouble(int)}
     */
    @Deprecated
    static ColumnAggregator average(int column) {
        return averageDouble(column);
    }

    /**
     * @since 0.6
     */
    static <S extends Number> ColumnAggregator<S, Double> averageDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.averageDouble(), index -> index.position(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static <S extends Number> ColumnAggregator<S, Double> averageDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.averageDouble(), index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #medianDouble(String)}
     */
    @Deprecated
    static ColumnAggregator median(String column) {
        return medianDouble(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #medianDouble(int)}
     */
    @Deprecated
    static ColumnAggregator median(int column) {
        return medianDouble(column);
    }

    /**
     * @since 0.6
     */
    static <S extends Number> ColumnAggregator<S, Double> medianDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.medianDouble(), index -> index.position(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static <S extends Number> ColumnAggregator<S, Double> medianDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.medianDouble(), index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #sumLong(String)}
     */
    static ColumnAggregator sum(String column) {
        return sumLong(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #sumLong(int)}
     */
    static ColumnAggregator sum(int column) {
        return sumLong(column);
    }

    /**
     * @since 0.6
     */
    static <S extends Number> ColumnAggregator<S, Long> sumLong(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumLong(), index -> index.position(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static <S extends Number> ColumnAggregator<S, Long> sumLong(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumLong(), index -> column,
                index -> index.getLabel(column)
        );
    }

    static <S extends Number> ColumnAggregator<S, Double> sumDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDouble(), index -> index.position(column),
                index -> column
        );
    }

    static <S extends Number> ColumnAggregator<S, Double> sumDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDouble(), index -> column,
                index -> index.getLabel(column)
        );
    }

    static <S> ColumnAggregator<S, String> concat(String column, String delimiter) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter), index -> index.position(column),
                index -> column
        );
    }

    static <S> ColumnAggregator<S, String> concat(int column, String delimiter) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter), index -> column,
                index -> index.getLabel(column)
        );
    }

    static <S> ColumnAggregator<S, String> concat(String column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter, prefix, suffix), index -> index.position(column),
                index -> column
        );
    }

    static <S> ColumnAggregator<S, String> concat(int column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter, prefix, suffix), index -> column,
                index -> index.getLabel(column)
        );
    }

    static <S> ColumnAggregator<S, List<S>> list(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.list(), index -> index.position(column),
                index -> column
        );
    }

    static <S> ColumnAggregator<S, List<S>> list(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.list(), index -> column,
                index -> index.getLabel(column)
        );
    }

    static <S> ColumnAggregator<S, Set<S>> set(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.set(), index -> index.position(column),
                index -> column
        );
    }

    static <S> ColumnAggregator<S, Set<S>> set(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.set(), index -> column,
                index -> index.getLabel(column)
        );
    }

    static <S, T> ColumnAggregator<S, T> of(String column, Collector<S, ?, T> aggregator) {
        return new ColumnAggregator<>(
                SeriesAggregator.of(aggregator), index -> index.position(column),
                index -> column
        );
    }

    static <S, T> ColumnAggregator<S, T> of(int column, Collector<S, ?, T> aggregator) {
        return new ColumnAggregator<>(
                SeriesAggregator.of(aggregator), index -> column,
                index -> index.getLabel(column)
        );
    }

    Index aggregateIndex(Index columns);

    Object[] aggregate(DataFrame df);
}
