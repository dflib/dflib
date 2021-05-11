package com.nhl.dflib.seriesexp.filter;

import com.nhl.dflib.*;

/**
 * An expression that applies a filter to the DataFrame before delegating processing to another expression.
 *
 * @since 0.11
 */
public class PreFilteredNumericSeriesExp<N extends Number> implements NumericSeriesExp<N> {

    private final SeriesCondition filter;
    private final SeriesExp<N> delegate;

    public PreFilteredNumericSeriesExp(SeriesCondition filter, SeriesExp<N> delegate) {
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
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return delegate.getName(df);
    }

    @Override
    public Class<N> getType() {
        return delegate.getType();
    }
}
