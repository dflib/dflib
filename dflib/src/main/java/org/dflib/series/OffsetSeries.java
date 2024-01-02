package org.dflib.series;

import org.dflib.Series;

/**
 * @since 0.9
 */
public abstract class OffsetSeries<T> extends ObjectSeries<T> {

    protected Series<T> delegate;
    protected T filler;

    public OffsetSeries(Series<T> delegate, T filler) {
        super(delegate.getNominalType());

        this.delegate = delegate;
        this.filler = filler;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Series<T> fillNulls(T value) {
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        return materialize().fillNullsFromSeries(values);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        return materialize().fillNullsForward();
    }
}
