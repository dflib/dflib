package org.dflib.exp.agg;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp1;

import java.util.function.Function;

/**
 * Evaluates {@link Exp} and then aggregates the result into a single-value series.
 *
 * @since 2.0.0
 */
public class ReduceExp1<F, T> extends Exp1<F, T> {

    private final Function<Series<F>, T> op;

    public ReduceExp1(String opName, Class<T> type, Exp<F> exp, Function<Series<F>, T> op) {
        super(opName, type, exp);
        this.op = op;
    }

    @Override
    protected Series<T> doEval(Series<F> s) {
        T val = op.apply(s);
        return Series.ofVal(val, 1);
    }
}
