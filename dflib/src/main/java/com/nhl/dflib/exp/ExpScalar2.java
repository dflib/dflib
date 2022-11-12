package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

/**
 * A binary expression with Exp on the left side and a scalar - on the right.
 *
 * @since 0.11
 */
public abstract class ExpScalar2<L, R, T> implements Exp<T> {

    protected final String opName;
    protected final Class<T> type;
    protected final Exp<L> left;
    protected final R right;

    public ExpScalar2(String opName, Class<T> type, Exp<L> left, R right) {
        this.opName = opName;
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String toQL() {
        return left.toQL() + " " + opName + " " + right;
    }

    @Override
    public String toQL(DataFrame df) {
        return left.toQL(df) + " " + opName + " " + right;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return doEval(left.eval(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return doEval(left.eval(s));
    }

    protected abstract Series<T> doEval(Series<L> left);
}
