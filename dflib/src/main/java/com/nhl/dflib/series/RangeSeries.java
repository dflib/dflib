package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.range.Range;

/**
 * A specialized Series that maps to a slice of another Series.
 */
public class RangeSeries<T> extends ObjectSeries<T> {

    private Series<T> delegate;
    private int offset;
    private int size;

    public RangeSeries(Series<T> delegate, int offset, int size) {
        super(delegate.getNominalType());
        this.delegate = delegate;
        this.offset = offset;
        this.size = size;
        Range.checkRange(offset, size, delegate.size());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return delegate.get(offset + index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {

        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        delegate.copyTo(to, this.offset + fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        Object[] range = new Object[size];
        delegate.copyTo(range, this.offset, 0, size);
        return new ArraySeries<>((T[]) range);
    }

    @Override
    public Series<T> fillNulls(T value) {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNullsFromSeries(values);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNullsForward();
    }
}
