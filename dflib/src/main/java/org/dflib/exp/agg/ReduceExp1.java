package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
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
    private final Condition filter;

    public ReduceExp1(String opName, Class<T> type, Exp<F> exp, Function<Series<F>, T> op, Condition filter) {
        super(opName, type, exp);
        this.op = op;
        this.filter = filter;
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return Series.ofVal(reduce(s), s.size());
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return Series.ofVal(reduce(df), df.height());
    }

    @Override
    public T reduce(DataFrame df) {
        return op.apply(exp.eval(filter != null ? df.rows(filter).select() : df));
    }

    @Override
    public T reduce(Series<?> s) {
        return op.apply(exp.eval(filter != null ? s.select(filter) : s));
    }
}
