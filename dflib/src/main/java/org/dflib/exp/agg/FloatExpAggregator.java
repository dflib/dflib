package org.dflib.exp.agg;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.FloatSingleValueSeries;

import java.util.function.Function;

/**
 * @since 1.1.0
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
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"
        float val = aggregator.apply(s);
        return new FloatSingleValueSeries(val, 1);
    }
}
