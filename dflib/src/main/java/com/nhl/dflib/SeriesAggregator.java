package com.nhl.dflib;

import com.nhl.dflib.aggregate.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        return new SimpleSeriesAggregator<>("first", s -> s.size() == 0 ? null : s.get(0));
    }

    static <S> SeriesAggregator<S, Long> countLong() {
        return new SimpleSeriesAggregator<>("countLong", s -> Long.valueOf(s.size()));
    }

    static <S> SeriesAggregator<S, Integer> countInt() {
        return new SimpleSeriesAggregator<>("countInt", s -> s.size());
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> averageDouble() {
        return of("averageDouble", Collectors.averagingDouble(v -> v.doubleValue()));
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> medianDouble() {
        return of("medianDouble", AggregatorFunctions.medianCollector());
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Integer> sumInt() {
        return of("sumInt", Collectors.summingInt(v -> v.intValue()));
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Long> sumLong() {
        return of("sumLong", Collectors.summingLong(v -> v.longValue()));
    }

    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> sumDouble() {
        return of("sumDouble", Collectors.summingDouble(v -> v.doubleValue()));
    }

    /**
     * @since 0.11
     */
    static SeriesAggregator<BigDecimal, BigDecimal> sumBigDecimal() {
        return new SimpleSeriesAggregator<>("sumBigDecimal", BigDecimalSeriesSum::sum);
    }

    /**
     * @since 0.11
     */
    static SeriesAggregator<BigDecimal, BigDecimal> sumBigDecimal(int resultScale, RoundingMode resultRoundingMode) {
        return new SimpleSeriesAggregator<>("sumBigDecimal", s -> BigDecimalSeriesSum.sum(s, resultScale, resultRoundingMode));
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> SeriesAggregator<S, S> max() {
        return new SimpleSeriesAggregator<>("max", SeriesMinMax::max);
    }

    /**
     * @since 0.7
     */
    static <S extends Comparable<S>> SeriesAggregator<S, S> min() {
        return new SimpleSeriesAggregator<>("min", SeriesMinMax::min);
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Integer> maxInt() {
        return new SimpleSeriesAggregator<S, Integer>("maxInt", SeriesMinMax::maxInt);
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Long> maxLong() {
        return new SimpleSeriesAggregator<S, Long>("maxLong", SeriesMinMax::maxLong);
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> maxDouble() {
        return new SimpleSeriesAggregator<S, Double>("maxDouble", SeriesMinMax::maxDouble);
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Integer> minInt() {
        return new SimpleSeriesAggregator<S, Integer>("minInt", SeriesMinMax::minInt);
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Long> minLong() {
        return new SimpleSeriesAggregator<S, Long>("minLong", SeriesMinMax::minLong);
    }

    /**
     * @since 0.7
     */
    // TODO: special handling of primitive series to avoid boxing/unboxing
    static <S extends Number> SeriesAggregator<S, Double> minDouble() {
        return new SimpleSeriesAggregator<S, Double>("minDouble", SeriesMinMax::minDouble);
    }

    static <S> SeriesAggregator<S, String> concat(String delimiter) {
        return new CollectorSeriesAggregator<>(
                "concat",
                AggregatorFunctions.mappedCollector(Collectors.joining(delimiter), String::valueOf)
        );
    }

    static <S> SeriesAggregator<S, String> concat(String delimiter, String prefix, String suffix) {
        return new CollectorSeriesAggregator<>(
                "concat",
                AggregatorFunctions.mappedCollector(Collectors.joining(delimiter, prefix, suffix), String::valueOf)
        );
    }

    static <S> SeriesAggregator<S, List<S>> list() {
        return new SimpleSeriesAggregator<>("list", s -> (List<S>) s.toList());
    }

    static <S> SeriesAggregator<S, Set<S>> set() {
        return new SimpleSeriesAggregator<>("set", s -> (Set<S>) s.toSet());
    }

    static <S, T> SeriesAggregator<S, T> of(String aggregateLabel, Collector<S, ?, T> aggregator) {
        return new CollectorSeriesAggregator<>(aggregateLabel, aggregator);
    }

    T aggregate(Series<? extends S> s);

    String aggregateLabel();

    /**
     * Ensures that the aggregated column in a DataFrame will be named using the provided label. Only applicable for
     * aggregating GroupBy.
     */
    SeriesAggregator<S, T> named(String newAggregateLabel);
}
