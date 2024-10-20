package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.LongSingleValueSeries;

import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class LongReduceExp1<F> extends Exp1<F, Long> implements NumExp<Long> {

    private final Function<Series<F>, Long> aggregator;
    private final Condition filter;

    public LongReduceExp1(String opName, Exp<F> exp, Function<Series<F>, Long> aggregator, Condition filter) {
        super(opName, Long.class, exp);
        this.aggregator = aggregator;
        this.filter = filter;
    }

    @Override
    public Series<Long> eval(Series<?> s) {
        return super.eval(filter != null ? s.select(filter) : s);
    }

    @Override
    public Series<Long> eval(DataFrame df) {
        return super.eval(filter != null ? df.rows(filter).select() : df);
    }

    @Override
    protected Series<Long> doEval(Series<F> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"

        long val = aggregator.apply(s);
        return new LongSingleValueSeries(val, 1);
    }
}
