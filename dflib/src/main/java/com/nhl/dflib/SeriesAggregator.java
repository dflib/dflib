package com.nhl.dflib;

import com.nhl.dflib.aggregate.AggregatorFunctions;
import com.nhl.dflib.aggregate.CollectorSeriesAggregator;
import com.nhl.dflib.aggregate.SimpleSeriesAggregator;

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

    // TODO: perhaps there's a more efficient way to extract a List from Series than CollectorSeriesAggregator?
    static <S> SeriesAggregator<S, List<S>> list() {
        return of("list", Collectors.toList());
    }

    // TODO: perhaps there's a more efficient way to extract a Set from Series than CollectorSeriesAggregator?
    static <S> SeriesAggregator<S, Set<S>> set() {
        return of("set", Collectors.toSet());
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
