package org.dflib.builder;

import org.dflib.Extractor;

public class SelfExtractor<T> implements Extractor<T, T> {

    @Override
    public Extractor<T, T> compact() {
        return new CompactSelfExtractor();
    }

    @Override
    public void extractAndStore(T from, ValueStore<T> to) {
        to.push(from);
    }

    @Override
    public void extractAndStore(T from, ValueStore<T> to, int toPos) {
        to.replace(toPos, from);
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
