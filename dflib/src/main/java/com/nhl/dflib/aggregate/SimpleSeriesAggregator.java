package com.nhl.dflib.aggregate;

import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesAggregator;

import java.util.function.Function;

/**
 * @since 0.6
 */
public class SimpleSeriesAggregator<S, T> implements SeriesAggregator<S, T> {

    private String aggregateLabel;
    private Function<Series<S>, T> aggregator;

    public SimpleSeriesAggregator(String aggregateLabel, Function<Series<S>, T> aggregator) {
        this.aggregateLabel = aggregateLabel;
        this.aggregator = aggregator;
    }

    @Override
    public String aggregateLabel() {
        return aggregateLabel;
    }

    @Override
    public SeriesAggregator<S, T> named(String newAggregateLabel) {
        return new SimpleSeriesAggregator<>(newAggregateLabel, aggregator);
    }

    @Override
    public T aggregate(Series<? extends S> s) {
        return aggregator.apply((Series<S>) s);
    }
}
