package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

/**
 * A unary expression with an {@link Exp} argument.
 *
 * @since 0.11
 */
public abstract class Exp1<F, T> implements Exp<T> {

    private final String opName;
    protected final Exp<F> exp;
    private final Class<T> type;

    public Exp1(String opName, Class<T> type, Exp<F> exp) {
        this.opName = opName;
        this.exp = exp;
        this.type = type;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return opName + "(" + exp.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return opName + "(" + exp.toQL(df) + ")";
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return doEval(exp.eval(s));
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return doEval(exp.eval(df));
    }

    protected abstract Series<T> doEval(Series<F> s);
}
