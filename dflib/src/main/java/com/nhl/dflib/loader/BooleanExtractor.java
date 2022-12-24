package com.nhl.dflib.loader;

import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.accumulator.ValueAccum;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.accumulator.ValueStore;

/**
 * @since 0.16
 */
public class BooleanExtractor<F> implements ValueExtractor<F, Boolean> {

    private final BooleanValueMapper<F> mapper;

    public BooleanExtractor(BooleanValueMapper<F> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void extract(F from, ValueStore<Boolean> to) {
        to.pushBoolean(mapper.map(from));
    }

    @Override
    public void extract(F from, ValueStore<Boolean> to, int toPos) {
        to.replaceBoolean(toPos, mapper.map(from));
    }

    @Override
    public ValueAccum<Boolean> createAccumulator(int capacity) {
        return new BooleanAccumulator(capacity);
    }
}