package org.dflib.builder;

import org.dflib.Series;

/**
 * @since 1.0.0-M19
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
        return Series.ofVal(value, size);
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
