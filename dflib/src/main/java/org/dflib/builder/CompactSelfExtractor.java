package org.dflib.builder;

import org.dflib.Extractor;

import java.util.HashMap;
import java.util.Map;

public class CompactSelfExtractor<T> implements Extractor<T, T> {

    private final Map<T, T> valueCache;

    public CompactSelfExtractor() {
        // extractors are single-threaded, can use a thread-unsafe map
        this.valueCache = new HashMap<>();
    }

    @Override
    public Extractor<T, T> compact() {
        return this;
    }

    @Override
    public void extractAndStore(T from, ValueStore<T> to) {
        to.push(ValueCompactor.get(valueCache, from));
    }

    @Override
    public void extractAndStore(T from, ValueStore<T> to, int toPos) {
        to.replace(toPos, ValueCompactor.get(valueCache, from));
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
