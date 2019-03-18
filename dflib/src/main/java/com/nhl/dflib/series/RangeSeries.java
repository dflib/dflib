package com.nhl.dflib.series;

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

        if (from > delegate.size() ) {
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
}
