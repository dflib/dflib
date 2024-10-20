package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.IntSingleValueSeries;

import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class IntReduceExp1<F> extends Exp1<F, Integer> implements NumExp<Integer> {

    private final Function<Series<F>, Integer> aggregator;
    private final Condition filter;

    public IntReduceExp1(String opName, Exp<F> exp, Function<Series<F>, Integer> aggregator, Condition filter) {
        super(opName, Integer.class, exp);
        this.aggregator = aggregator;
        this.filter = filter;
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        return super.eval(filter != null ? s.select(filter) : s);
    }

    @Override
    public Series<Integer> eval(DataFrame df) {
        return super.eval(filter != null ? df.rows(filter).select() : df);
    }

    @Override
    protected Series<Integer> doEval(Series<F> s) {

        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"

        int val = aggregator.apply(s);
        return new IntSingleValueSeries(val, 1);
    }
}
