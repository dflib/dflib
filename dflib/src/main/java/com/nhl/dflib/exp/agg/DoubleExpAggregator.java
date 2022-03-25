package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.Exp1;
import com.nhl.dflib.series.DoubleSingleValueSeries;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class DoubleExpAggregator<F> extends Exp1<F, Double> implements NumExp<Double> {

    private final Function<Series<F>, Double> aggregator;

    public DoubleExpAggregator(String opName, Exp<F> exp, Function<Series<F>, Double> aggregator) {
        super(opName, Double.class, exp);
        this.aggregator = aggregator;
    }

    @Override
    protected Series<Double> doEval(Series<F> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"
        double val = aggregator.apply(s);
        return new DoubleSingleValueSeries(val, 1);
    }
}
