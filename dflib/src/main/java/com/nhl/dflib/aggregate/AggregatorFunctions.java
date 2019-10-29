package com.nhl.dflib.aggregate;

import com.nhl.dflib.ValueMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class AggregatorFunctions {

    public static <S, M, A, T> Collector<S, A, T> mappedCollector(Collector<M, A, T> collector, ValueMapper<S, M> mapper) {

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

    public static <S extends Number> Collector<S, List<Double>, Double> medianCollector() {
        return new AggregationCollector<>(
                ArrayList::new,
                (list, d) -> {
                    if (d != null) {
                        list.add(d.doubleValue());
                    }
                },
                medianWithSideEffects());
    }

    private static Function<List<Double>, Double> medianWithSideEffects() {
        return list -> {

            if (list.isEmpty()) {
                return 0.;
            }

            switch (list.size()) {
                case 0:
                    return 0.;
                case 1:
                    return list.get(0);
                default:
                    // side effect - resorting the list; since the list is not exposed outside of "medianCollector", this should
                    // not cause any issues
                    Collections.sort(list);

                    int m = list.size() / 2;

                    int odd = list.size() % 2;
                    if (odd == 1) {
                        return list.get(m);
                    }

                    double d1 = list.get(m - 1);
                    double d2 = list.get(m);
                    return d1 + (d2 - d1) / 2.;
            }
        };
    }
}
