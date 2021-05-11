package com.nhl.dflib;

import com.nhl.dflib.aggregate.LongCountAggregator;
import com.nhl.dflib.aggregate.SimpleSeriesAggregator;
import com.nhl.dflib.seriesexp.agg.AggregatorFunctions;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        return Exp.$col("", Number.class).agg(AggregatorFunctions.averageDouble()).as("medianDouble");
    }

    static SeriesExp<Integer> sumInt() {
        return Exp.$col("", Number.class).agg(AggregatorFunctions.sumInt()).as("sumInt");
    }

    static SeriesExp<Long> sumLong() {
        return new SimpleSeriesAggregator<>("sumLong", AggregatorFunctions.sumLong());
    }

    static SeriesExp<Double> sumDouble() {
        return new SimpleSeriesAggregator<>("sumDouble", AggregatorFunctions.sumDouble());
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> SeriesExp<S> max() {
        return new SimpleSeriesAggregator<>("max", AggregatorFunctions.<S>max());
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> SeriesExp<S> min() {
        return new SimpleSeriesAggregator<>("min", AggregatorFunctions.<S>min());
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Integer> maxInt() {
        return new SimpleSeriesAggregator<>("maxInt", AggregatorFunctions.maxInt());
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Long> maxLong() {
        return new SimpleSeriesAggregator<>("maxLong", AggregatorFunctions.maxLong());
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Double> maxDouble() {
        return new SimpleSeriesAggregator<>("maxDouble", AggregatorFunctions.maxDouble());
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Integer> minInt() {
        return new SimpleSeriesAggregator<>("minInt", AggregatorFunctions.minInt());
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Long> minLong() {
        return new SimpleSeriesAggregator<>("minLong", AggregatorFunctions.minLong());
    }

    /**
     * @since 0.7
     */
    static SeriesExp<Double> minDouble() {
        return new SimpleSeriesAggregator<>("minDouble", AggregatorFunctions.minDouble());
    }

    static <S> SeriesAggregator<S, String> concat(String delimiter) {
        return new SimpleSeriesAggregator<>("concat", AggregatorFunctions.concat(delimiter));
    }

    static <S> SeriesAggregator<S, String> concat(String delimiter, String prefix, String suffix) {
        return new SimpleSeriesAggregator<>("concat", AggregatorFunctions.concat(delimiter, prefix, suffix));
    }

    static <S> SeriesAggregator<S, List<S>> list() {
        return new SimpleSeriesAggregator<>("list", Series::toList);
    }

    static <S> SeriesAggregator<S, Set<S>> set() {
        return new SimpleSeriesAggregator<>("set", Series::toSet);
    }
}
