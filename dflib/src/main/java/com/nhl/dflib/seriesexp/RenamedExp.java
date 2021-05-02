package com.nhl.dflib.seriesexp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;

/**
 * @since 0.11
 */
public class RenamedExp<V> implements SeriesExp<V> {

    private final String name;
    private final SeriesExp<V> delegate;

    public RenamedExp(String name, SeriesExp<V> delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<V> getType() {
        return delegate.getType();
    }

    @Override
    public Series<V> eval(DataFrame df) {
        return delegate.eval(df);
    }
}
