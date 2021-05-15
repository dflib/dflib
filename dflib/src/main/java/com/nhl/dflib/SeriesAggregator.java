package com.nhl.dflib;

import com.nhl.dflib.aggregate.LongCountAggregator;
import com.nhl.dflib.exp.agg.*;

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
        return Exp.$col("", Number.class).agg(DoubleAggregators::avg).as("averageDouble");
    }

    static Exp<Double> medianDouble() {
        return Exp.$col("", Number.class).agg(DoubleAggregators::median).as("medianDouble");
    }

    static Exp<Integer> sumInt() {
        return Exp.$col("", Number.class).agg(IntAggregators::sum).as("sumInt");
    }

    static Exp<Long> sumLong() {
        return Exp.$col("", Number.class).agg(LongAggregators::sum).as("sumLong");
    }

    static Exp<Double> sumDouble() {
        return Exp.$col("", Number.class).agg(DoubleAggregators::sum).as("sumDouble");
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> Exp<S> max() {
        return Exp.<S>$col("").agg(ComparableAggregators::max).as("max");
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> Exp<S> min() {
        return Exp.<S>$col("").agg(ComparableAggregators::min).as("min");
    }

    /**
     * @since 0.7
     */
    static Exp<Integer> maxInt() {
        return Exp.<Integer>$col("").agg(IntAggregators::max).as("maxInt");
    }

    /**
     * @since 0.7
     */
    static Exp<Long> maxLong() {
        return Exp.<Long>$col("").agg(LongAggregators::max).as("maxLong");
    }

    /**
     * @since 0.7
     */
    static Exp<Double> maxDouble() {
        return Exp.<Double>$col("").agg(DoubleAggregators::max).as("maxDouble");
    }

    /**
     * @since 0.7
     */
    static Exp<Integer> minInt() {
        return Exp.<Integer>$col("").agg(IntAggregators::min).as("minInt");
    }

    /**
     * @since 0.7
     */
    static Exp<Long> minLong() {
        return Exp.<Long>$col("").agg(LongAggregators::min).as("minLong");
    }

    /**
     * @since 0.7
     */
    static Exp<Double> minDouble() {
        return Exp.<Double>$col("").agg(DoubleAggregators::min).as("minDouble");
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
