package com.nhl.dflib.exp;

import com.nhl.dflib.*;

/**
 * @since 0.11
 */
public abstract class Condition2<L, R> implements Condition {

    private final String opName;
    protected final Exp<L> left;
    protected final Exp<R> right;

    public Condition2(String opName, Exp<L> left, Exp<R> right) {
        this.opName = opName;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return left.getName() + opName + right.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return left.getName(df) + opName + right.getName(df);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(left.eval(df), right.eval(df));
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(left.eval(s), right.eval(s));
    }

    protected abstract BooleanSeries doEval(Series<L> left, Series<R> right);
}
