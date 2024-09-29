package org.dflib.builder;

import org.dflib.Extractor;

/**
 * @since 1.0.0-M19
 */
public class SingleValueExtractor<F, T> implements Extractor<F, T> {

    private final T value;

    public SingleValueExtractor(T value) {
        this.value = value;
    }

    @Override
    public Extractor<F, T> compact() {
        return this;
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
