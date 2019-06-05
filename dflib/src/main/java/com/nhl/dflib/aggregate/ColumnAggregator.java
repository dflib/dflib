package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesAggregator;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * An aggregator whose source is a column in a DataFrame. The actual aggregation operation is defined as a
 * {@link SeriesAggregator} internally.
 */
public class ColumnAggregator<S, T> implements Aggregator<T> {

    private SeriesAggregator<S, T> aggregator;
    private ToIntFunction<Index> sourceColumnLocator;
    private Function<Index, String> targetColumnNamer;

    public ColumnAggregator(
            SeriesAggregator<S, T> aggregator,
            ToIntFunction<Index> sourceColumnLocator,
            Function<Index, String> targetColumnNamer) {

        this.sourceColumnLocator = sourceColumnLocator;
        this.aggregator = aggregator;
        this.targetColumnNamer = targetColumnNamer;
    }

    protected T aggregate(Series<S> s) {
        return aggregator.aggregate(s);
    }

    @Override
    public T aggregate(DataFrame df) {
        int pos = sourceColumnLocator.applyAsInt(df.getColumnsIndex());
        return aggregate(df.getColumn(pos));
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return targetColumnNamer.apply(columnIndex);
    }

    @Override
    public Aggregator<T> named(String newAggregateLabel) {
        return new ColumnAggregator<>(aggregator, sourceColumnLocator, i -> newAggregateLabel);
    }
}
