package org.dflib.exp.agg;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp1;

import java.util.function.Function;

/**
 * Evaluates {@link Exp} and then aggregates the result into a single-value series.
 */
public class ExpAggregator<F, T> extends Exp1<F, T> {

    private final Function<Series<F>, T> aggregator;

    public ExpAggregator(String opName, Class<T> type, Exp<F> exp, Function<Series<F>, T> aggregator) {
        super(opName, type, exp);
        this.aggregator = aggregator;
    }

    @Override
    protected Series<T> doEval(Series<F> s) {
        T val = aggregator.apply(s);
        return Series.ofVal(val, 1);
    }
}
