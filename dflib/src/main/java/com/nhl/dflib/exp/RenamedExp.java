package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

/**
 * @since 0.11
 */
public class RenamedExp<V> implements Exp<V> {

    private final String name;
    private final Exp<V> delegate;

    public RenamedExp(String name, Exp<V> delegate) {
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
