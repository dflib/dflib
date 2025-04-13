package org.dflib.builder;

import org.dflib.Extractor;

public class CompactSelfExtractor<T> implements Extractor<T, T> {

    private final ValueCompactor<T> valueCompactor;

    public CompactSelfExtractor() {
        this.valueCompactor = new ValueCompactor<>();
    }

    @Override
    public Extractor<T, T> compact() {
        return this;
    }

    @Override
    public void extractAndStore(T from, ValueStore<T> to) {
        to.push(valueCompactor.get(from));
    }

    @Override
    public void extractAndStore(T from, ValueStore<T> to, int toPos) {
        to.replace(toPos, valueCompactor.get(from));
    }

    @Override
    public ValueAccum<T> createAccum(int capacity) {
        return new ObjectAccum<>(capacity);
    }

    @Override
    public ValueHolder<T> createHolder() {
        return new ObjectHolder<>();
    }
}
