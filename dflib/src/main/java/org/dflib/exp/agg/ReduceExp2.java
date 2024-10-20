package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp2;

import java.util.function.BiFunction;

/**
 * @since 2.0.0
 */
public class ReduceExp2<L, R, T> extends Exp2<L, R, T> {

    private final BiFunction<Series<L>, Series<R>, T> op;
    private final Condition filter;

    public ReduceExp2(String opName, Class<T> type, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, T> op, Condition filter) {
        super(opName, type, left, right);
        this.op = op;
        this.filter = filter;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return super.eval(filter != null ? df.rows(filter).select() : df);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return super.eval(filter != null ? s.select(filter) : s);
    }

    @Override
    protected Series<T> doEval(Series<L> left, Series<R> right) {
        T val = op.apply(left, right);
        return Series.ofVal(val, 1);
    }

    @Override
    public String toQL() {
        // aggregators are usually functions, not operators. So redefine super...
        return opName + "(" + left.toQL() + "," + right.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        // aggregators are usually functions, not operators. So redefine super...
        return opName + "(" + left.toQL(df) + "," + right.toQL(df) + ")";
    }
}
