package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.RowPredicate;
import com.nhl.dflib.row.RowProxy;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * @since 0.7
 */
public class CollectorFilteredAggregator<S, A, T> implements Aggregator<T> {

    private RowPredicate rowFilter;
    private Collector<S, A, T> collector;
    private Function<Index, String> targetColumnNamer;

    public CollectorFilteredAggregator(
            RowPredicate rowFilter,
            Collector<S, A, T> collector,
            Function<Index, String> targetColumnNamer) {

        this.rowFilter = rowFilter;
        this.collector = collector;
        this.targetColumnNamer = targetColumnNamer;
    }

    @Override
    public T aggregate(DataFrame df) {

        BiConsumer accumulator = collector.accumulator();
        A accumResult = collector.supplier().get();

        for (RowProxy r : df) {
            if (rowFilter.test(r)) {
                accumulator.accept(accumResult, r);
            }
        }

        return collector.finisher().apply(accumResult);
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return targetColumnNamer.apply(columnIndex);
    }

    @Override
    public Aggregator named(String newAggregateLabel) {
        return new CollectorFilteredAggregator<>(rowFilter, collector, i -> newAggregateLabel);
    }
}
