package org.dflib.exp.num;

import org.dflib.Series;
import org.dflib.series.ObjectSeries;

import java.util.Objects;

class ResolvedNominalSeries<T extends Number> extends ObjectSeries<T> {

    private final Series<T> delegate;
    private final boolean hasNulls;

    ResolvedNominalSeries(Class<? extends Number> nominalType, Series<T> delegate, boolean hasNulls) {
        super(Objects.requireNonNull(nominalType));
        this.delegate = Objects.requireNonNull(delegate);
        this.hasNulls = hasNulls;
    }

    boolean hasNulls() {
        return hasNulls;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public T get(int index) {
        return delegate.get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        delegate.copyTo(to, fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        Series<T> materialized = delegate.materialize();
        return materialized == delegate ? this : new ResolvedNominalSeries<>((Class<? extends Number>) nominalType, materialized, hasNulls);
    }

    @Override
    public Series<T> fillNulls(T value) {
        return delegate.fillNulls(value);
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        return delegate.fillNullsFromSeries(values);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return delegate.fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        return delegate.fillNullsForward();
    }
}
