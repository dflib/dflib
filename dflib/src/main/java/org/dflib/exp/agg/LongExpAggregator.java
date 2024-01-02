package org.dflib.exp.agg;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.LongSingleValueSeries;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class LongExpAggregator<F> extends Exp1<F, Long> implements NumExp<Long> {

    private final Function<Series<F>, Long> aggregator;

    public LongExpAggregator(String opName, Exp<F> exp, Function<Series<F>, Long> aggregator) {
        super(opName, Long.class, exp);
        this.aggregator = aggregator;
    }

    @Override
    protected Series<Long> doEval(Series<F> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"

        long val = aggregator.apply(s);
        return new LongSingleValueSeries(val, 1);
    }
}
