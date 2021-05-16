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

    public ExpAggregator(Exp<S> exp, Function<Series<S>, T> aggregator) {
        this.exp = exp;
        this.aggregator = aggregator;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<T> getType() {
        // TODO: ....
        return (Class<T>) Object.class;
    }

    @Override
    public String getName() {
        // TODO: wrap in the name of the aggregator function
        return exp.getName();
    }

    @Override
    public String getName(DataFrame df) {
        // TODO: wrap in the name of the aggregator function
        return exp.getName(df);
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
