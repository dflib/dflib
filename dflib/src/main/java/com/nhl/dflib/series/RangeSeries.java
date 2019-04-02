package com.nhl.dflib.series;

import com.nhl.dflib.Printers;
import com.nhl.dflib.Series;

public class RangeSeries<T> implements Series<T> {

    private Series<T> delegate;
    private int from;
    private int size;

    public RangeSeries(Series<T> delegate, int from, int size) {
        this.delegate = delegate;
        this.from = from;
        this.size = size;

        // TODO: allow negative ranges to reference range from the tail?
        if (from < 0) {
            throw new IllegalArgumentException("Negative 'from' index: " + from);
        }

        if (from > delegate.size()) {
            throw new IllegalArgumentException("From is out of range: " + from + " (" + delegate.size() + ")");
        }

        if (from + size > delegate.size()) {
            throw new IllegalArgumentException("Size is out of range: " + (from + size) + " > " + delegate.size() + ")");
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        if (index > size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return delegate.get(from + index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {

        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        delegate.copyTo(to, this.from + fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        Object[] range = new Object[size];
        delegate.copyTo(range, this.from, 0, size);
        return new ArraySeries<>((T[]) range);
    }

    @Override
    public Series<T> fillNulls(T value) {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNulls(value);
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

    @Override
    public String toString() {
        return Printers.inline.print(new StringBuilder("RangeSeries ["), this).append("]").toString();
    }
}
