package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Series;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * @since 0.11
 */
public class CollectorAggregator<S, A, T> implements Function<Series<S>, T> {

    private final Collector<S, A, T> collector;

    public CollectorAggregator(Collector<S, A, T> collector) {
        this.collector = collector;
    }

    @Override
    public T apply(Series<S> s) {
        BiConsumer accum = collector.accumulator();
        A result = collector.supplier().get();

        int len = s.size();
        for (int i = 0; i < len; i++) {
            accum.accept(result, s.get(i));
        }

        return collector.finisher().apply(result);
    }
}
