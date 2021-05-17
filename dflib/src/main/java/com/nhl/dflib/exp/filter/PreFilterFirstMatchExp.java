package com.nhl.dflib.exp.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;

/**
 * An aggregator similar to {@link PreFilteredExp}, only optimized for returning the first matched item.
 *
 * @since 0.11
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
        return delegate.eval(index < 0 ? df.selectRows() : df.selectRows(index));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        // Optimization - using "firstMatch" instead of full "eval"
        int index = filter.firstMatch(s);
        return delegate.eval(index < 0 ? s.select() : s.select(index));
    }
}
