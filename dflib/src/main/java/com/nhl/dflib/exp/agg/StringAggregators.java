package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Series;
import com.nhl.dflib.ValueMapper;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @since 0.11
 */
public class StringAggregators {

    public static <S> Function<Series<S>, String> concat(String delimiter) {
        return new CollectorAggregator<>(mappedCollector(Collectors.joining(delimiter), String::valueOf));
    }

    public static <S> Function<Series<S>, String> concat(String delimiter, String prefix, String suffix) {
        return new CollectorAggregator<>(mappedCollector(Collectors.joining(delimiter, prefix, suffix), String::valueOf));
    }

    protected static <S, M, A, T> Collector<S, A, T> mappedCollector(Collector<M, A, T> collector, ValueMapper<S, M> mapper) {

        return new Collector<S, A, T>() {
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
    }
}
