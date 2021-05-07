package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.function.Function;

/**
 * An aggregator whose source is a column in a DataFrame. The actual aggregation operation is defined as a
 * {@link SeriesAggregator} internally.
 */
public class ColumnAggregator<S, T> implements SeriesExp<T> {

    private final Class<T> type;
    private SeriesAggregator<S, T> aggregator;
    private SeriesExp<S> columnExp;
    private Function<Index, String> targetColumnNamer;

    public ColumnAggregator(
            SeriesAggregator<S, T> aggregator,
            SeriesExp<S> columnExp,
            Function<Index, String> targetColumnNamer) {

        this.columnExp = columnExp;
        this.aggregator = aggregator;
        this.targetColumnNamer = targetColumnNamer;
    }

    @Override
    public Class<T> getType() {
        return null;
    }

    protected T aggregate(Series<S> s) {
        return aggregator.aggregate(s);
    }

    @Override
    public Series<T> eval(DataFrame df) {
        T val = aggregate(columnExp.eval(df));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName(DataFrame df) {
        return targetColumnNamer.apply(df.getColumnsIndex());
    }
}
