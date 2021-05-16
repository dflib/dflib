package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

/**
 * A binary expression with two {@link Exp} arguments.
 *
 * @since 0.11
 */
public abstract class Exp2<L, R, T> implements Exp<T> {

    private final String opName;
    private final Class<T> type;
    protected final Exp<L> left;
    protected final Exp<R> right;

    public Exp2(String opName, Class<T> type, Exp<L> left, Exp<R> right) {
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
        return left.getName() + " " + opName + " " + right.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return left.getName(df) + " " + opName + " " + right.getName(df);
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return doEval(left.eval(df), right.eval(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return doEval(left.eval(s), right.eval(s));
    }

    protected abstract Series<T> doEval(Series<L> left, Series<R> right);
}
