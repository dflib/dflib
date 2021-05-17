package com.nhl.dflib.exp.filter;

import com.nhl.dflib.*;

/**
 * An expression that applies a filter to the DataFrame before delegating processing to another expression.
 *
 * @since 0.11
 */
public class PreFilteredNumExp<N extends Number> implements NumExp<N> {

    private final Condition filter;
    private final Exp<N> delegate;

    public PreFilteredNumExp(Condition filter, Exp<N> delegate) {
        this.filter = filter;
        this.delegate = delegate;
    }

    @Override
    public Series<N> eval(DataFrame df) {
        return delegate.eval(df.selectRows(filter));
    }

    @Override
    public Series<N> eval(Series<?> s) {
        return delegate.eval(s.select(filter));
    }

    @Override
    public String toQL() {
        return delegate.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return delegate.toQL(df);
    }

    @Override
    public Class<N> getType() {
        return delegate.getType();
    }

    @Override
    public String toString() {
        return toQL();
    }
}
