package org.dflib.exp.filter;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.Condition;
import org.dflib.Exp;

/**
 * An aggregator similar to {@link PreFilteredExp}, only optimized for returning the first matched item.
 */
public class PreFilterFirstMatchExp<T> implements Exp<T> {

    private final Condition filter;
    private final Exp<T> delegate;

    public PreFilterFirstMatchExp(Condition filter, Exp<T> delegate) {
        this.filter = filter;
        this.delegate = delegate;
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
        // Optimization - using "firstMatch" instead of full "eval"
        int index = filter.firstMatch(df);
        DataFrame prefiltered = index < 0 ? DataFrame.empty(df.getColumnsIndex()) : df.rows(index).select();
        return delegate.eval(prefiltered);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        // Optimization - using "firstMatch" instead of full "eval"
        int index = filter.firstMatch(s);
        return delegate.eval(index < 0 ? s.select() : s.select(index));
    }
}
