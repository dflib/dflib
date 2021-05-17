package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

import java.util.Objects;

/**
 * @since 0.11
 */
public class AsExp<T> extends ExpScalar2<T, String, T> {

    public AsExp(String name, Exp<T> delegate) {
        super("as", delegate.getType(), delegate, name);
    }

    @Override
    protected Series<T> doEval(Series<T> left, String right) {
        return left;
    }

    @Override
    public Exp<T> as(String name) {
        return Objects.equals(name, this.opName) ? this : new AsExp<>(name, left);
    }

    @Override
    public String getColumnName() {
        return right;
    }

    @Override
    public String getColumnName(DataFrame df) {
        return right;
    }
}
