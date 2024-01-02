package org.dflib.builder;

import org.dflib.Extractor;
import org.dflib.ValueMapper;

/**
 * @since 0.8
 */
public class ObjectExtractor<F, T> implements Extractor<F, T> {

    private final ValueMapper<F, T> mapper;

    public ObjectExtractor(ValueMapper<F, T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void extractAndStore(F from, ValueStore<T> to) {
        to.push(mapper.map(from));
    }

    @Override
    public void extractAndStore(F from, ValueStore<T> to, int toPos) {
        to.replace(toPos, mapper.map(from));
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
