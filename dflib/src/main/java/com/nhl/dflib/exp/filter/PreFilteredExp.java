package com.nhl.dflib.exp.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;

/**
 * An expression that applies a filter to the DataFrame before delegating processing to another expression.
 *
 * @since 0.11
 */
public class PreFilteredExp<T> implements Exp<T> {

    private final Condition filter;
    private final Exp<T> delegate;

    public PreFilteredExp(Condition filter, Exp<T> delegate) {
        this.filter = filter;
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        return getName();
    }


    @Override
    public Class<T> getType() {
        return delegate.getType();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return delegate.getName(df);
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return delegate.eval(df.selectRows(filter));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return delegate.eval(s.select(filter));
    }
}
