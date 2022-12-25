package com.nhl.dflib.builder;

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
    public void push(T v) {
        this.v = v;
    }

    @Override
    public void pushToStore(ValueStore<T> to) {
        to.push(v);
    }

    @Override
    public void pushToStore(ValueStore<T> to, int pos) {
        to.replace(pos, v);
    }
}
