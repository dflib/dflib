package com.nhl.dflib.seriesexp.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;

/**
 * An expression that applies a filter to the DataFrame before delegating processing to another expression.
 *
 * @since 0.11
 */
public class PreFilteredSeriesExp<T> implements SeriesExp<T> {

    private final SeriesCondition filter;
    private final SeriesExp<T> delegate;

    public PreFilteredSeriesExp(SeriesCondition filter, SeriesExp<T> delegate) {
        this.filter = filter;
        this.delegate = delegate;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return delegate.eval(df.selectRows(filter));
    }

    @Override
    public String getName(DataFrame df) {
        return delegate.getName(df);
    }

    @Override
    public Class<T> getType() {
        return delegate.getType();
    }
}
