package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;

import java.util.Objects;

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
    public String getName(DataFrame df) {
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

    @Override
    public Series<T> eval(Series<?> s) {
        return delegate.eval(s);
    }

    @Override
    public SeriesExp<T> as(String name) {
        return Objects.equals(name, this.name) ? this : new RenamedExp<>(name, delegate);
    }
}
