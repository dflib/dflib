package org.dflib.exp.agg;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.function.Function;

/**
 * Evaluates {@link Exp} and then aggregates the result into a single-value series.
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
        return toQL();
    }

    @Override
    public Class<T> getType() {
        // TODO: ....
        return (Class<T>) Object.class;
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
        return Series.ofVal(val, 1);
    }
}
