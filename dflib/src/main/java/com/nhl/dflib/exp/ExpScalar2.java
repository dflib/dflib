package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

/**
 * A binary expression with one Exp and one scalar arguments.
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
        return getName();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return left.getName() + " " + opName + " " + right;
    }

    @Override
    public String getName(DataFrame df) {
        return left.getName(df) + " " + opName + " " + right;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return doEval(left.eval(df), right);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return doEval(left.eval(s), right);
    }

    protected abstract Series<T> doEval(Series<L> left, R right);
}
