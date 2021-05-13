package com.nhl.dflib;

import com.nhl.dflib.aggregate.LongCountAggregator;
import com.nhl.dflib.exp.agg.AggregatorFunctions;

import java.util.List;
import java.util.Set;

/**
 * @see Exp
 * @since 0.6
 * @deprecated since 0.11 in favor of aggregating {@link Exp}
 */
@Deprecated
public interface SeriesAggregator {

    /**
     * Returns the first value in a Series. Useful in extracting key columns during group by, as presumably all values
     * in such Series are the same.
     *
     * @return a new SeriesAggregator
     */
    static <S> Exp<S> first() {
        return (Exp<S>) Exp.$col("").first().as("first");
    }

    static Exp<Long> countLong() {
        return new LongCountAggregator().as("contLong");
    }

    static Exp<Integer> countInt() {
        return Exp.count().as("countInt");
    }

    static Exp<Double> averageDouble() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.avgDouble()).as("averageDouble");
    }

    static Exp<Double> medianDouble() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.medianDouble()).as("medianDouble");
    }

    static Exp<Integer> sumInt() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.sumInt()).as("sumInt");
    }

    static Exp<Long> sumLong() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.sumLong()).as("sumLong");
    }

    static Exp<Double> sumDouble() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.sumDouble()).as("sumDouble");
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> Exp<S> max() {
        return Exp.<S>$col("").agg(AggregatorFunctions.max()).as("max");
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> Exp<S> min() {
        return Exp.<S>$col("").agg(AggregatorFunctions.min()).as("min");
    }

    /**
     * @since 0.7
     */
    static Exp<Integer> maxInt() {
        return Exp.<Integer>$col("").agg(AggregatorFunctions.maxInt()).as("maxInt");
    }

    /**
     * @since 0.7
     */
    static Exp<Long> maxLong() {
        return Exp.<Long>$col("").agg(AggregatorFunctions.maxLong()).as("maxLong");
    }

    /**
     * @since 0.7
     */
    static Exp<Double> maxDouble() {
        return Exp.<Double>$col("").agg(AggregatorFunctions.maxDouble()).as("maxDouble");
    }

    /**
     * @since 0.7
     */
    static Exp<Integer> minInt() {
        return Exp.<Integer>$col("").agg(AggregatorFunctions.minInt()).as("minInt");
    }

    /**
     * @since 0.7
     */
    static Exp<Long> minLong() {
        return Exp.<Long>$col("").agg(AggregatorFunctions.minLong()).as("minLong");
    }

    /**
     * @since 0.7
     */
    static Exp<Double> minDouble() {
        return Exp.<Double>$col("").agg(AggregatorFunctions.minDouble()).as("minDouble");
    }

    static Exp<String> concat(String delimiter) {
        return Exp.$col("").vConcat(delimiter).as("concat");
    }

    static Exp<String> concat(String delimiter, String prefix, String suffix) {
        return Exp.$col("").vConcat(delimiter, prefix, suffix).as("concat");
    }

    static <S> Exp<List<S>> list() {
        return ((Exp<S>) Exp.$col("")).list().as("list");
    }

    static <S> Exp<Set<S>> set() {
        return ((Exp<S>) Exp.$col("")).set().as("set");
    }
}
