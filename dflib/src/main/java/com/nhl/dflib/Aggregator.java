package com.nhl.dflib;

import com.nhl.dflib.aggregate.AggregatorBuilder;
import com.nhl.dflib.aggregate.DataFrameAggregator;
import com.nhl.dflib.aggregate.LongCountAggregator;
import com.nhl.dflib.seriesexp.agg.AggregatorFunctions;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Defines an aggregation operation to produce a single value from a DataFrame.
 *
 * @deprecated since 0.11 in favor of aggregating {@link SeriesExp}
 */
@Deprecated
public interface Aggregator {

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     */
    static <T> SeriesExp<T> first(String column) {
        return (SeriesExp<T>) Exp.$col(column).first();
    }

    /**
     * Returns the first value in an aggregation range. Useful in extracting key columns during group by, as presumably
     * all values in the range are the same.
     */
    static <T> SeriesExp<T> first(int column) {
        return (SeriesExp<T>) Exp.$col(column).first();
    }

    /**
     * Creates an aggregator to count DataFrame rows.
     *
     * @since 0.6
     */
    static SeriesExp<Long> countLong() {
        return new LongCountAggregator().as("_long_count");
    }

    /**
     * Creates an aggregator to count DataFrame rows.
     *
     * @since 0.6
     */
    static SeriesExp<Integer> countInt() {
        return Exp.count().as("_int_count");
    }

    /**
     * @since 0.6
     */
    static SeriesExp<Double> averageDouble(String column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.averageDouble());
    }

    /**
     * @since 0.6
     */
    static SeriesExp<Double> averageDouble(int column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.averageDouble());
    }

    /**
     * @since 0.6
     */
    static SeriesExp<Double> medianDouble(String column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.medianDouble());
    }

    /**
     * @since 0.6
     */
    static SeriesExp<Double> medianDouble(int column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.medianDouble());
    }

    /**
     * @since 0.6
     */
    static SeriesExp<Long> sumLong(String column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.sumLong());
    }

    /**
     * @since 0.6
     */
    static SeriesExp<Long> sumLong(int column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.sumLong());
    }

    /**
     * @since 0.6
     */
    static SeriesExp<Integer> sumInt(String column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.sumInt());
    }

    /**
     * @since 0.6
     */
    static SeriesExp<Integer> sumInt(int column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.sumInt());
    }

    static SeriesExp<Double> sumDouble(String column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.sumDouble());
    }

    static SeriesExp<Double> sumDouble(int column) {
        return ((SeriesExp<Number>) Exp.$col(column)).agg(AggregatorFunctions.sumDouble());
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> SeriesExp<T> max(String column) {
        return ((SeriesExp<T>) Exp.$col(column)).agg(AggregatorFunctions.max());
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> SeriesExp<T> max(int column) {
        return ((SeriesExp<T>) Exp.$col(column)).agg(AggregatorFunctions.max());
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> SeriesExp<T> min(int column) {
        return ((SeriesExp<T>) Exp.$col(column)).agg(AggregatorFunctions.min());
    }

    /**
     * @since 0.7
     */
    static <T extends Comparable<T>> SeriesExp<T> min(String column) {
        return ((SeriesExp<T>) Exp.$col(column)).agg(AggregatorFunctions.min());
    }

    static SeriesExp<String> concat(String column, String delimiter) {
        return Exp.$col(column).vConcat(delimiter);
    }

    static SeriesExp<String> concat(int column, String delimiter) {
        return Exp.$col(column).vConcat(delimiter);
    }

    static SeriesExp<String> concat(String column, String delimiter, String prefix, String suffix) {
        return Exp.$col(column).vConcat(delimiter, prefix, suffix);
    }

    static SeriesExp<String> concat(int column, String delimiter, String prefix, String suffix) {
        return Exp.$col(column).vConcat(delimiter, prefix, suffix);
    }

    static <S> SeriesExp<List<S>> list(String column) {
        return ((SeriesExp<S>) Exp.$col(column)).list();
    }

    static <S> SeriesExp<List<S>> list(int column) {
        return ((SeriesExp<S>) Exp.$col(column)).list();
    }

    static <S> SeriesExp<Set<S>> set(String column) {
        return ((SeriesExp<S>) Exp.$col(column)).set();
    }

    static <S> SeriesExp<Set<S>> set(int column) {
        return ((SeriesExp<S>) Exp.$col(column)).set();
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

    static <S, A, T> SeriesExp<T> of(String column, Collector<S, A, T> aggregator) {
        return ((SeriesExp<S>) Exp.$col(column)).agg(AggregatorFunctions.fromCollector(aggregator));
    }

    static <S, T> SeriesExp<T> of(int column, Collector<S, ?, T> aggregator) {
        return ((SeriesExp<S>) Exp.$col(column)).agg(AggregatorFunctions.fromCollector(aggregator));
    }

    /**
     * Creates an Aggregator based on a custom function that takes the entire DataFrame.
     *
     * @since 0.6
     */
    static <T> SeriesExp<T> of(Function<DataFrame, T> aggregator) {
        return new DataFrameAggregator<>(
                aggregator,
                index -> "of"
        );
    }
}
