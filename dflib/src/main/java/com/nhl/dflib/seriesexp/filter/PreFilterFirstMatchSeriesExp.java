package com.nhl.dflib.seriesexp.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;

/**
 * An aggregator similar to {@link PreFilteredSeriesExp}, only optimized for returning the first matched item.
 *
 * @since 0.11
 */
public class PreFilterFirstMatchSeriesExp<T> implements SeriesExp<T> {

    private final SeriesCondition filter;
    private final SeriesExp<T> delegate;

    public PreFilterFirstMatchSeriesExp(SeriesCondition filter, SeriesExp<T> delegate) {
        this.filter = filter;
        this.delegate = delegate;
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

    @Override
    public String getName(DataFrame df) {
        return delegate.getName(df);
    }

    @Override
    public Class<T> getType() {
        return delegate.getType();
    }
}
