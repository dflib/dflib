package org.dflib.builder;

import org.dflib.Extractor;
import org.dflib.ValueMapper;

public class CompactObjectExtractor<F, T> implements Extractor<F, T> {

    private final ValueMapper<F, T> mapper;
    private final ValueCompactor<T> valueCompactor;

    public CompactObjectExtractor(ValueMapper<F, T> mapper) {
        this.mapper = mapper;
        this.valueCompactor = new ValueCompactor<>();
    }

    @Override
    public Extractor<F, T> compact() {
        return this;
    }

    @Override
    public void extractAndStore(F from, ValueStore<T> to) {
        to.push(valueCompactor.get(mapper.map(from)));
    }

    @Override
    public void extractAndStore(F from, ValueStore<T> to, int toPos) {
        to.replace(toPos, valueCompactor.get(mapper.map(from)));
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
