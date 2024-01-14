package org.dflib.exp.filter;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.Condition;
import org.dflib.Exp;

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
        return toQL();
    }


    @Override
    public Class<T> getType() {
        return delegate.getType();
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
    public Series<T> eval(DataFrame df) {
        return delegate.eval(df.rows(filter).select());
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return delegate.eval(s.select(filter));
    }
}
