package com.nhl.dflib;

import com.nhl.dflib.aggregate.LongCountAggregator;
import com.nhl.dflib.seriesexp.agg.AggregatorFunctions;

import java.util.List;
import java.util.Set;

/**
 * @see Exp
 * @since 0.6
 * @deprecated since 0.11 in favor of aggregating {@link SeriesExp}
 */
@Deprecated
public interface SeriesAggregator {

    /**
     * Returns the first value in a Series. Useful in extracting key columns during group by, as presumably all values
     * in such Series are the same.
     *
     * @return a new SeriesAggregator
     */
    static <S> SeriesExp<S> first() {
        return (SeriesExp<S>) Exp.$col("").first().as("first");
    }

    static SeriesExp<Long> countLong() {
        return new LongCountAggregator().as("contLong");
    }

    static SeriesExp<Integer> countInt() {
        return Exp.count().as("countInt");
    }

    static SeriesExp<Double> averageDouble() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.averageDouble()).as("averageDouble");
    }

    static SeriesExp<Double> medianDouble() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.medianDouble()).as("medianDouble");
    }

    static SeriesExp<Integer> sumInt() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.sumInt()).as("sumInt");
    }

    static SeriesExp<Long> sumLong() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.sumLong()).as("sumLong");
    }

    static SeriesExp<Double> sumDouble() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.sumDouble()).as("sumDouble");
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> SeriesExp<S> max() {
        return Exp.<S>$col("").agg(AggregatorFunctions.max()).as("max");
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> SeriesExp<S> min() {
        return Exp.<S>$col("").agg(AggregatorFunctions.min()).as("min");
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Integer> maxInt() {
        return Exp.<Integer>$col("").agg(AggregatorFunctions.maxInt()).as("maxInt");
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Long> maxLong() {
        return Exp.<Long>$col("").agg(AggregatorFunctions.maxLong()).as("maxLong");
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Double> maxDouble() {
        return Exp.<Double>$col("").agg(AggregatorFunctions.maxDouble()).as("maxDouble");
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Integer> minInt() {
        return Exp.<Integer>$col("").agg(AggregatorFunctions.minInt()).as("minInt");
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Long> minLong() {
        return Exp.<Long>$col("").agg(AggregatorFunctions.minLong()).as("minLong");
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Double> minDouble() {
        return Exp.<Double>$col("").agg(AggregatorFunctions.minDouble()).as("minDouble");
    }

    static SeriesExp<String> concat(String delimiter) {
        return Exp.$col("").vConcat(delimiter).as("concat");
    }

    static SeriesExp<String> concat(String delimiter, String prefix, String suffix) {
        return Exp.$col("").vConcat(delimiter, prefix, suffix).as("concat");
    }

    static <S> SeriesExp<List<S>> list() {
        return ((SeriesExp<S>) Exp.$col("")).list().as("list");
    }

    static <S> SeriesExp<Set<S>> set() {
        return ((SeriesExp<S>) Exp.$col("")).set().as("set");
    }
}
