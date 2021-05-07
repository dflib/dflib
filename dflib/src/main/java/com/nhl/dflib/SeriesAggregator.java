package com.nhl.dflib;

import com.nhl.dflib.seriesexp.agg.AggregatorFunctions;
import com.nhl.dflib.aggregate.SimpleSeriesAggregator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

/**
 * @since 0.6
 */
public interface SeriesAggregator<S, T> {

    /**
     * Returns the first value in a Series. Useful in extracting key columns during group by, as presumably all values
     * in such Series are the same.
     *
     * @return a new SeriesAggregator
     */
    static <S> SeriesAggregator<S, S> first() {
        return new SimpleSeriesAggregator<>("first", AggregatorFunctions.first());
    }

    static <S> SeriesAggregator<S, Long> countLong() {
        return new SimpleSeriesAggregator<>("countLong", s -> (long) s.size());
    }

    static <S> SeriesAggregator<S, Integer> countInt() {
        return new SimpleSeriesAggregator<>("countInt", Series::size);
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> averageDouble() {
        return new SimpleSeriesAggregator<>("averageDouble", AggregatorFunctions.averageDouble());
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> medianDouble() {
        return new SimpleSeriesAggregator<>("medianDouble", AggregatorFunctions.medianDouble());
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Integer> sumInt() {
        return new SimpleSeriesAggregator<>("sumInt", AggregatorFunctions.sumInt());
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Long> sumLong() {
        return new SimpleSeriesAggregator<>("sumLong", AggregatorFunctions.sumLong());
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> sumDouble() {
        return new SimpleSeriesAggregator<>("sumDouble", AggregatorFunctions.sumDouble());
    }

    /**
     * @since 0.11
     */
    static SeriesAggregator<BigDecimal, BigDecimal> sumDecimal() {
        return new SimpleSeriesAggregator<>("sumDecimal", AggregatorFunctions.sumDecimal());
    }

    /**
     * @since 0.11
     */
    static SeriesAggregator<BigDecimal, BigDecimal> sumDecimal(int resultScale, RoundingMode resultRoundingMode) {
        return new SimpleSeriesAggregator<>("sumDecimal", AggregatorFunctions.sumDecimal(resultScale, resultRoundingMode));
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> SeriesAggregator<S, S> max() {
        return new SimpleSeriesAggregator<>("max", AggregatorFunctions.<S>max());
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> SeriesAggregator<S, S> min() {
        return new SimpleSeriesAggregator<>("min", AggregatorFunctions.<S>min());
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Integer> maxInt() {
        return new SimpleSeriesAggregator<>("maxInt", AggregatorFunctions.maxInt());
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Long> maxLong() {
        return new SimpleSeriesAggregator<>("maxLong", AggregatorFunctions.maxLong());
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> maxDouble() {
        return new SimpleSeriesAggregator<>("maxDouble", AggregatorFunctions.maxDouble());
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Integer> minInt() {
        return new SimpleSeriesAggregator<>("minInt", AggregatorFunctions.minInt());
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Long> minLong() {
        return new SimpleSeriesAggregator<>("minLong", AggregatorFunctions.minLong());
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> minDouble() {
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

    T aggregate(Series<? extends S> s);

    String aggregateLabel();

    /**
     * Ensures that the aggregated column in a DataFrame will be named using the provided label. Only applicable for
     * aggregating GroupBy.
     */
    SeriesAggregator<S, T> named(String newAggregateLabel);
}
