package com.nhl.dflib.exp;

import com.nhl.dflib.*;

/**
 * @since 0.11
 */
public abstract class Condition1<F> implements Condition {

    private final String opName;
    protected final Exp<F> exp;

    protected Condition1(String opName, Exp<F> exp) {
        this.opName = opName;
        this.exp = exp;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return opName + exp.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return opName + exp.getName(df);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(exp.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(exp.eval(s));
    }

    protected abstract BooleanSeries doEval(Series<F> s);
}
