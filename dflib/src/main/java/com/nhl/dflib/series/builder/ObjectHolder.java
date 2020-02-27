package com.nhl.dflib.series.builder;

/**
 * @since 0.8
 */
public class ObjectHolder<T> implements ValueHolder<T> {

    private T v;

    @Override
    public T get() {
        return v;
    }

    @Override
    public void set(T v) {
        this.v = v;
    }

    @Override
    public void store(Accumulator<T> accumulator) {
        accumulator.add(v);
    }
}
