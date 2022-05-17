package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.Exp1;
import com.nhl.dflib.series.FloatSingleValueSeries;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class FloatExpAggregator<F> extends Exp1<F, Float> implements NumExp<Float> {

    private final Function<Series<F>, Float> aggregator;

    public FloatExpAggregator(String opName, Exp<F> exp, Function<Series<F>, Float> aggregator) {
        super(opName, Float.class, exp);
        this.aggregator = aggregator;
    }

    @Override
    protected Series<Float> doEval(Series<F> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageFloat()"
        float val = aggregator.apply(s);
        return new FloatSingleValueSeries(val, 1);
    }
}
