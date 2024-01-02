package org.dflib.exp.agg;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.IntSingleValueSeries;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class IntExpAggregator<F> extends Exp1<F, Integer> implements NumExp<Integer> {

    private final Function<Series<F>, Integer> aggregator;

    public IntExpAggregator(String opName, Exp<F> exp, Function<Series<F>, Integer> aggregator) {
        super(opName, Integer.class, exp);
        this.aggregator = aggregator;
    }

    @Override
    protected Series<Integer> doEval(Series<F> s) {

        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"

        int val = aggregator.apply(s);
        return new IntSingleValueSeries(val, 1);
    }
}
