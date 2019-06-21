package com.nhl.dflib;

import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.aggregate.DataFrameAggregator;
import com.nhl.dflib.aggregate.IntCountAggregator;
import com.nhl.dflib.aggregate.LongCountAggregator;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Defines an aggregation operation to produce a single value from a DataFrame. Behind the scenes often this operation
 * works on a single column of data and is a facade to {@link SeriesAggregator}.
 */
public interface Aggregator<T> {

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     *
     * @param column
     * @return a new Aggregator
     */
    static <T> Aggregator<T> first(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.first(),
                index -> index.position(column),
                index -> column
        );
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     */
    static <T> Aggregator<T> first(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.first(),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * Creates an aggregator to count Series
     *
     * @since 0.6
     */
    static Aggregator<Long> countLong() {
        return new LongCountAggregator("_long_count");
    }

    /**
     * Creates an aggregator to calculate a sum of the specified column.
     *
     * @since 0.6
     */
    static Aggregator<Integer> countInt() {
        return new IntCountAggregator("_int_count");
    }

    /**
     * @deprecated since 0.6 in favor of {@link #averageDouble(String)}
     */
    @Deprecated
    static Aggregator average(String column) {
        return averageDouble(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #averageDouble(int)}
     */
    @Deprecated
    static Aggregator average(int column) {
        return averageDouble(column);
    }

    /**
     * @since 0.6
     */
    static Aggregator<Double> averageDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.averageDouble(),
                index -> index.position(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Double> averageDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.averageDouble(),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #medianDouble(String)}
     */
    @Deprecated
    static Aggregator median(String column) {
        return medianDouble(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #medianDouble(int)}
     */
    @Deprecated
    static Aggregator median(int column) {
        return medianDouble(column);
    }

    /**
     * @since 0.6
     */
    static Aggregator<Double> medianDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.medianDouble(),
                index -> index.position(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Double> medianDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.medianDouble(),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * @deprecated since 0.6 in favor of {@link #sumLong(String)}
     */
    static Aggregator sum(String column) {
        return sumLong(column);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #sumLong(int)}
     */
    static Aggregator sum(int column) {
        return sumLong(column);
    }

    /**
     * @since 0.6
     */
    static Aggregator<Long> sumLong(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumLong(),
                index -> index.position(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Long> sumLong(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumLong(),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Integer> sumInt(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumInt(),
                index -> index.position(column),
                index -> column
        );
    }

    /**
     * @since 0.6
     */
    static Aggregator<Integer> sumInt(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumInt(),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    static Aggregator<Double> sumDouble(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDouble(),
                index -> index.position(column),
                index -> column
        );
    }

    static Aggregator<Double> sumDouble(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.sumDouble(),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    static Aggregator<String> concat(String column, String delimiter) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter),
                index -> index.position(column),
                index -> column
        );
    }

    static Aggregator<String> concat(int column, String delimiter) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    static Aggregator<String> concat(String column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter, prefix, suffix),
                index -> index.position(column),
                index -> column
        );
    }

    static Aggregator<String> concat(int column, String delimiter, String prefix, String suffix) {
        return new ColumnAggregator<>(
                SeriesAggregator.concat(delimiter, prefix, suffix),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    static <S> Aggregator<List<S>> list(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.list(),
                index -> index.position(column),
                index -> column
        );
    }

    static <S> Aggregator<List<S>> list(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.list(),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    static <S> Aggregator<Set<S>> set(String column) {
        return new ColumnAggregator<>(
                SeriesAggregator.set(),
                index -> index.position(column),
                index -> column
        );
    }

    static <S> Aggregator<Set<S>> set(int column) {
        return new ColumnAggregator<>(
                SeriesAggregator.set(),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    static <T> Aggregator<T> of(String column, Collector<?, ?, T> aggregator) {
        return new ColumnAggregator<>(
                SeriesAggregator.of("of", aggregator),
                index -> index.position(column),
                index -> column
        );
    }

    static <T> Aggregator<T> of(int column, Collector<?, ?, T> aggregator) {
        return new ColumnAggregator<>(
                SeriesAggregator.of("of", aggregator),
                index -> column,
                index -> index.getLabel(column)
        );
    }

    /**
     * Creates an Aggregator based on a custom function that takes the entire DataFrame.
     *
     * @since 0.6
     */
    static <T> Aggregator<T> of(Function<DataFrame, T> aggregator) {
        return new DataFrameAggregator<>(
                aggregator,
                index -> "of"
        );
    }

    T aggregate(DataFrame df);

    String aggregateLabel(Index columnIndex);

    /**
     * Ensures that the aggregated column in a DataFrame will be named using the provided label. Only applicable for
     * aggregating GroupBy.
     */
    Aggregator<T> named(String newAggregateLabel);
}
