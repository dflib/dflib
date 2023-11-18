package com.nhl.dflib.builder;

import com.nhl.dflib.Extractor;

/**
 * @since 0.19
 */
public class SingleValueExtractor<F, T> implements Extractor<F, T> {

    private final T value;

    public SingleValueExtractor(T value) {
        this.value = value;
    }

    @Override
    public void extractAndStore(F from, ValueStore<T> to) {
        to.push(value);
    }

    @Override
    public void extractAndStore(F from, ValueStore<T> to, int toPos) {
        to.replace(toPos, value);
    }

    @Override
    public ValueAccum<T> createAccum(int capacity) {
        return new SingleValueAccum<>(value);
    }

    @Override
    public ValueHolder<T> createHolder() {
        return new ObjectHolder<>();
    }
}
