package com.nhl.dflib.loader;

import com.nhl.dflib.IntValueMapper;
import com.nhl.dflib.accumulator.ValueAccum;
import com.nhl.dflib.accumulator.IntAccumulator;
import com.nhl.dflib.accumulator.ValueStore;

/**
 * @since 0.8
 */
public class IntExtractor<F> implements ValueExtractor<F, Integer> {

    private final IntValueMapper<F> mapper;

    public IntExtractor(IntValueMapper<F> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void extract(F from, ValueStore<Integer> to) {
        to.pushInt(mapper.map(from));
    }

    @Override
    public void extract(F from, ValueStore<Integer> to, int toPos) {
        to.replaceInt(toPos, mapper.map(from));
    }

    @Override
    public ValueAccum<Integer> createAccumulator(int capacity) {
        return new IntAccumulator(capacity);
    }
}
