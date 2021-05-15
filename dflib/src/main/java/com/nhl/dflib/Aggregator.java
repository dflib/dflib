package com.nhl.dflib;

import com.nhl.dflib.aggregate.AggregatorBuilder;
import com.nhl.dflib.aggregate.DataFrameAggregator;
import com.nhl.dflib.aggregate.LongCountAggregator;
import com.nhl.dflib.exp.agg.*;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Defines an aggregation operation to produce a single value from a DataFrame.
 *
 * @deprecated since 0.11 in favor of aggregating {@link Exp}
 * @see Exp
 */
@Deprecated
public interface Aggregator {

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     */
    static <T> Exp<T> first(String column) {
        return (Exp<T>) Exp.$col(column).first();
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     */
    static <T> Exp<T> first(int column) {
        return (Exp<T>) Exp.$col(column).first();
    }

    /**
     * Creates an aggregator to count DataFrame rows.
     *
     * @since 0.6
     */
    static Exp<Long> countLong() {
        return new LongCountAggregator().as("_long_count");
    }

    /**
     * Creates an aggregator to count DataFrame rows.
     *
     * @since 0.6
     */
    static Exp<Integer> countInt() {
        return Exp.count().as("_int_count");
    }

    /**
     * @since 0.6
     */
    static Exp<Double> averageDouble(String column) {
        return Exp.$col(column, Number.class).agg(DoubleAggregators::avg);
    }

    /**
     * @since 0.6
     */
    static Exp<Double> averageDouble(int column) {
        return Exp.$col(column, Number.class).agg(DoubleAggregators::avg);
    }

    /**
     * @since 0.6
     */
    static Exp<Double> medianDouble(String column) {
        return Exp.$col(column, Number.class).agg(DoubleAggregators::median);
    }

    /**
     * @since 0.6
     */
    static Exp<Double> medianDouble(int column) {
        return Exp.$col(column, Number.class).agg(DoubleAggregators::median);
    }

    /**
     * @since 0.6
     */
    static Exp<Long> sumLong(String column) {
        return Exp.$col(column, Number.class).agg(LongAggregators::sum);
    }

    /**
     * @since 0.6
     */
    static Exp<Long> sumLong(int column) {
        return Exp.$col(column, Number.class).agg(LongAggregators::sum);
    }

    /**
     * @since 0.6
     */
    static Exp<Integer> sumInt(String column) {
        return Exp.$col(column, Number.class).agg(IntAggregators::sum);
    }

    /**
     * @since 0.6
     */
    static Exp<Integer> sumInt(int column) {
        return Exp.$col(column, Number.class).agg(IntAggregators::sum);
    }

    static Exp<Double> sumDouble(String column) {
        return Exp.$col(column, Number.class).agg(DoubleAggregators::sum);
    }

    static Exp<Double> sumDouble(int column) {
        return Exp.$col(column, Number.class).agg(DoubleAggregators::sum);
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> Exp<T> max(String column) {
        return Exp.<T>$col(column).agg(ComparableAggregators::max);
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> Exp<T> max(int column) {
        return Exp.<T>$col(column).agg(ComparableAggregators::max);
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> Exp<T> min(int column) {
        return Exp.<T>$col(column).agg(ComparableAggregators::min);
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> Exp<T> min(String column) {
        return Exp.<T>$col(column).agg(ComparableAggregators::min);
    }

    static Exp<String> concat(String column, String delimiter) {
        return Exp.$col(column).vConcat(delimiter);
    }

    static Exp<String> concat(int column, String delimiter) {
        return Exp.$col(column).vConcat(delimiter);
    }

    static Exp<String> concat(String column, String delimiter, String prefix, String suffix) {
        return Exp.$col(column).vConcat(delimiter, prefix, suffix);
    }

    static Exp<String> concat(int column, String delimiter, String prefix, String suffix) {
        return Exp.$col(column).vConcat(delimiter, prefix, suffix);
    }

    static <S> Exp<List<S>> list(String column) {
        return ((Exp<S>) Exp.$col(column)).list();
    }

    static <S> Exp<List<S>> list(int column) {
        return ((Exp<S>) Exp.$col(column)).list();
    }

    static <S> Exp<Set<S>> set(String column) {
        return ((Exp<S>) Exp.$col(column)).set();
    }

    static <S> Exp<Set<S>> set(int column) {
        return ((Exp<S>) Exp.$col(column)).set();
    }

    /**
     * Starts a builder of an aggregator that will prefilter DataFrame rows before applying an aggregation function.
     *
     * @since 0.7
     */
    static AggregatorBuilder filterRows(RowPredicate rowPredicate) {
        return new AggregatorBuilder().filterRows(rowPredicate);
    }

    /**
     * Starts a builder of an aggregator that will prefilter DataFrame rows before applying an aggregation function.
     *
     * @since 0.7
     */
    static <V> AggregatorBuilder filterRows(String columnLabel, ValuePredicate<V> filter) {
        return new AggregatorBuilder().filterRows(columnLabel, filter);
    }

    /**
     * Starts a builder of an aggregator that will prefilter DataFrame rows before applying an aggregation function.
     *
     * @since 0.7
     */
    static <V> AggregatorBuilder filterRows(int columnPos, ValuePredicate<V> filter) {
        return new AggregatorBuilder().filterRows(columnPos, filter);
    }

    static <S, A, T> Exp<T> of(String column, Collector<S, A, T> aggregator) {
        return ((Exp<S>) Exp.$col(column)).agg(CollectorAggregator.create(aggregator));
    }

    static <S, T> Exp<T> of(int column, Collector<S, ?, T> aggregator) {
        return ((Exp<S>) Exp.$col(column)).agg(CollectorAggregator.create(aggregator));
    }

    /**
     * Creates an Aggregator based on a custom function that takes the entire DataFrame.
     *
     * @since 0.6
     */
    static <T> Exp<T> of(Function<DataFrame, T> aggregator) {
        return new DataFrameAggregator<>(
                aggregator,
                index -> "of"
        );
    }
}
