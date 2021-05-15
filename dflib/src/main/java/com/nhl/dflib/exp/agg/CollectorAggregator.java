package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Series;
import com.nhl.dflib.ValueMapper;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * An adapter between Java {@link Collector} and an aggregation function.
 *
 * @since 0.11
 */
public class CollectorAggregator<S, A, T> implements Function<Series<S>, T> {

    private final Collector<S, A, T> collector;

    public static <S, T> Function<Series<S>, T> create(Collector<S, ?, T> collector) {
        return new CollectorAggregator<>(collector);
    }

    public static <S, M, A, T> Function<Series<S>, T> create(Collector<M, A, T> collector, ValueMapper<S, M> mapper) {

        Collector<S, A, T> mapped = new Collector<S, A, T>() {
            @Override
            public Supplier<A> supplier() {
                return collector.supplier();
            }

            @Override
            public BiConsumer<A, S> accumulator() {
                BiConsumer<A, M> accum = collector.accumulator();
                return (a, s) -> accum.accept(a, mapper.map(s));
            }

            @Override
            public BinaryOperator<A> combiner() {
                return collector.combiner();
            }

            @Override
            public Function<A, T> finisher() {
                return collector.finisher();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return collector.characteristics();
            }
        };

        return new CollectorAggregator<>(mapped);
    }

    private CollectorAggregator(Collector<S, A, T> collector) {
        this.collector = collector;
    }

    @Override
    public T apply(Series<S> s) {
        BiConsumer<A, S> accum = collector.accumulator();
        A result = collector.supplier().get();

        int len = s.size();
        for (int i = 0; i < len; i++) {
            accum.accept(result, s.get(i));
        }

        return collector.finisher().apply(result);
    }
}
