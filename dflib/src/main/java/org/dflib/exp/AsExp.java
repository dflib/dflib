package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.Objects;

public class AsExp<T> implements Exp<T>  {

    private final Exp<T> delegate;
    protected final String name;

    public AsExp(String name, Exp<T> delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AsExp<?> asExp = (AsExp<?>) o;
        return Objects.equals(delegate, asExp.delegate)
                && Objects.equals(name, asExp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, name);
    }

    public Series<T> eval(Series<?> s) {
        return delegate.eval(s);
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return delegate.eval(df);
    }

    @Override
    public T reduce(Series<?> s) {
        return delegate.reduce(s);
    }

    @Override
    public T reduce(DataFrame df) {
        return delegate.reduce(df);
    }

    @Override
    public Exp<T> as(String name) {
        return Objects.equals(name, this.name) ? this : new AsExp<>(name, delegate);
    }

    @Override
    public String getColumnName() {
        return name;
    }

    @Override
    public String getColumnName(DataFrame df) {
        return name;
    }

    @Override
    public Class<T> getType() {
        return delegate.getType();
    }

    @Override
    public String toQL() {
        return delegate.toQL() + " as " + name;
    }

    @Override
    public String toQL(DataFrame df) {
        return delegate.toQL(df) + " as " + name;
    }
}
