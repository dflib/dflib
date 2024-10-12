package org.dflib.builder;

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
}
