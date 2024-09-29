package org.dflib.builder;

import org.dflib.Extractor;
import org.dflib.ValueMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.8
 */
public class CompactObjectExtractor<F, T> implements Extractor<F, T> {

    private final ValueMapper<F, T> mapper;
    private final Map<T, T> valueCache;

    public CompactObjectExtractor(ValueMapper<F, T> mapper) {
        this.mapper = mapper;

        // extractors are single-threaded, can use a thread-unsafe map
        this.valueCache = new HashMap<>();
    }

    @Override
    public Extractor<F, T> compact() {
        return this;
    }

    @Override
    public void extractAndStore(F from, ValueStore<T> to) {
        to.push(ValueCompactor.get(valueCache, mapper.map(from)));
    }

    @Override
    public void extractAndStore(F from, ValueStore<T> to, int toPos) {
        to.replace(toPos, ValueCompactor.get(valueCache, mapper.map(from)));
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
