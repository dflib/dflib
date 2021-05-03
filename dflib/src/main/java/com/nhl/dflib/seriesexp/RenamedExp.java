package com.nhl.dflib.seriesexp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;

/**
 * @since 0.11
 */
public class RenamedExp<T> implements SeriesExp<T> {

    private final String name;
    private final SeriesExp<T> delegate;

    public RenamedExp(String name, SeriesExp<T> delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getType() {
        return delegate.getType();
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return delegate.eval(df);
    }
}
