package com.nhl.dflib.exp.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.function.Function;

/**
 * Evaluates {@link Exp} and then aggregates the result into a single-value series.
 *
 * @since 0.11
 */
public class ExpAggregator<S, T> implements Exp<T> {

    private final Exp<S> exp;
    private final Function<Series<S>, T> aggregator;
    private final Class<T> type;

    public ExpAggregator(Exp<S> exp, Function<Series<S>, T> aggregator) {
        this(exp,aggregator, (Class<T>) Object.class);
    }

    public ExpAggregator(Exp<S> exp, Function<Series<S>, T> aggregator,Class<T> type) {
        this.exp = exp;
        this.aggregator = aggregator;
        this.type = type;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String toQL() {
        // TODO: wrap in the name of the aggregator function
        return exp.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        // TODO: wrap in the name of the aggregator function
        return exp.toQL(df);
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return aggregate(extract(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return aggregate(extract(s));
    }

    protected Series<S> extract(DataFrame df) {
        return exp.eval(df);
    }

    protected Series<S> extract(Series<?> s) {
        return exp.eval(s);
    }

    protected Series<T> aggregate(Series<S> s) {
        T val = aggregator.apply(s);
        return new SingleValueSeries<>(val, 1);
    }
}
