package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;

import java.util.function.Function;

/**
 * An aggregator whose source is a column in a DataFrame. The actual aggregation operation is defined as a
 * {@link SeriesAggregator} internally.
 */
public class ColumnAggregator<S, T> implements Aggregator<T> {

    private SeriesAggregator<S, T> aggregator;
    private SeriesExp<S> sourceColumnExp;
    private Function<Index, String> targetColumnNamer;

    public ColumnAggregator(
            SeriesAggregator<S, T> aggregator,
            SeriesExp<S> sourceColumnExp,
            Function<Index, String> targetColumnNamer) {

        this.sourceColumnExp = sourceColumnExp;
        this.aggregator = aggregator;
        this.targetColumnNamer = targetColumnNamer;
    }

    protected T aggregate(Series<S> s) {
        return aggregator.aggregate(s);
    }

    @Override
    public T aggregate(DataFrame df) {
        return aggregate(sourceColumnExp.eval(df));
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return targetColumnNamer.apply(columnIndex);
    }

    @Override
    public Aggregator<T> named(String newAggregateLabel) {
        return new ColumnAggregator<>(aggregator, sourceColumnExp, i -> newAggregateLabel);
    }
}
