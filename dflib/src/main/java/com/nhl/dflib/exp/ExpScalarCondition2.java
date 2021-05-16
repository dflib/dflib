package com.nhl.dflib.exp;

import com.nhl.dflib.*;

/**
 * @since 0.11
 */
public abstract class ExpScalarCondition2<L, R> implements Condition {

    private final String opName;
    protected final Exp<L> left;
    protected final R right;

    public ExpScalarCondition2(String opName, Exp<L> left, R right) {
        this.opName = opName;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return left.getName() + opName + right;
    }

    @Override
    public String getName(DataFrame df) {
        return left.getName(df) + opName + right;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(left.eval(df), right);
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(left.eval(s), right);
    }

    protected abstract BooleanSeries doEval(Series<L> left, R right);
}
