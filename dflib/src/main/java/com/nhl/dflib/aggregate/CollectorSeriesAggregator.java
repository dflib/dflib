package com.nhl.dflib.aggregate;

import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesAggregator;

import java.util.function.BiConsumer;
import java.util.stream.Collector;

/**
 * A SeriesAggregator based on Java Streams collector.
 *
 * @param <S>
 * @param <T>
 * @since 0.6
 */
public class CollectorSeriesAggregator<S, A, T> implements SeriesAggregator<S, T> {

    private String aggregateLabel;
    private Collector<S, A, T> collector;

    public CollectorSeriesAggregator(String aggregateLabel, Collector<S, A, T> collector) {
        this.aggregateLabel = aggregateLabel;
        this.collector = collector;
    }

    @Override
    public T aggregate(Series<? extends S> s) {

        BiConsumer accumulator = collector.accumulator();
        A accumResult = collector.supplier().get();

        int len = s.size();
        for (int i = 0; i < len; i++) {
            accumulator.accept(accumResult, s.get(i));
        }

        return collector.finisher().apply(accumResult);
    }

    @Override
    public String aggregateLabel() {
        return aggregateLabel;
    }

    @Override
    public SeriesAggregator<S, T> named(String newAggregateLabel) {
        return new CollectorSeriesAggregator<>(newAggregateLabel, collector);
    }
}
