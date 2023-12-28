package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.Exp1;
import com.nhl.dflib.series.LongSingleValueSeries;

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
