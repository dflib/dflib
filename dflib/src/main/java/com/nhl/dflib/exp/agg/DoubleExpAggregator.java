package com.nhl.dflib.exp.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class DoubleExpAggregator<S> implements NumExp<Double> {

    private final Exp<S> exp;
    private final Function<Series<S>, Double> aggregator;

    public DoubleExpAggregator(Exp<S> exp, Function<Series<S>, Double> aggregator) {
        this.exp = exp;
        this.aggregator = aggregator;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public String getName() {
        return exp.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return exp.getName(df);
    }

    @Override
    public Series<Double> eval(DataFrame df) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"
        double val = aggregator.apply(exp.eval(df));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public Series<Double> eval(Series<?> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"
        double val = aggregator.apply(exp.eval(s));
        return new SingleValueSeries<>(val, 1);
    }
}
