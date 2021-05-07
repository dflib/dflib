package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesAggregator;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.function.Function;

/**
 * An aggregator whose source is a column in a DataFrame. The actual aggregation operation is defined as a
 * {@link SeriesAggregator} internally.
 */
public class ColumnAggregator<S, T> implements SeriesExp<T> {

    private final SeriesExp<S> exp;
    private final Function<Series<S>, T> aggregator;

    public ColumnAggregator(SeriesExp<S> exp, Function<Series<S>, T> aggregator) {
        this.exp = exp;
        this.aggregator = aggregator;
    }

    @Override
    public Class<T> getType() {
        // TODO: ....
        return (Class<T>) Object.class;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        T val = aggregator.apply(exp.eval(df));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName(DataFrame df) {
        return exp.getName(df);
    }
}
