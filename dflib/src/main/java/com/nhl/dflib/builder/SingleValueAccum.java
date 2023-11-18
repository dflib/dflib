package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.19
 */
public class SingleValueAccum<T> implements ValueAccum<T> {

    private final T value;
    private int size;

    public SingleValueAccum(T value) {
        this.value = value;
        this.size = 0;
    }

    @Override
    public Series<T> toSeries() {
        return new SingleValueSeries<>(value, size);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void push(T v) {
        size++;
    }

    @Override
    public void replace(int pos, T v) {
        // do nothing...
    }
}
