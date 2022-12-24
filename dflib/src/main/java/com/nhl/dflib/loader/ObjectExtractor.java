package com.nhl.dflib.loader;

import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.accumulator.ValueAccum;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.accumulator.ValueStore;

/**
 * @since 0.8
 */
public class ObjectExtractor<F, T> implements ValueExtractor<F, T> {

    private final ValueMapper<F, T> mapper;

    public ObjectExtractor(ValueMapper<F, T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void extract(F from, ValueStore<T> to) {
        to.push(mapper.map(from));
    }

    @Override
    public void extract(F from, ValueStore<T> to, int toPos) {
        to.replace(toPos, mapper.map(from));
    }

    @Override
    public ValueAccum<T> createAccumulator(int capacity) {
        return new ObjectAccumulator<>(capacity);
    }
}
