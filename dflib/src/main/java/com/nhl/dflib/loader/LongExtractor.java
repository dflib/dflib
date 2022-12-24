package com.nhl.dflib.loader;

import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.accumulator.ValueAccum;
import com.nhl.dflib.accumulator.LongAccumulator;
import com.nhl.dflib.accumulator.ValueStore;

/**
 * @since 0.8
 */
public class LongExtractor<F> implements ValueExtractor<F, Long> {

    private final LongValueMapper<F> mapper;

    public LongExtractor(LongValueMapper<F> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void extract(F from, ValueStore<Long> to) {
        to.pushLong(mapper.map(from));
    }

    @Override
    public void extract(F from, ValueStore<Long> to, int toPos) {
        to.replaceLong(toPos, mapper.map(from));
    }

    @Override
    public ValueAccum<Long> createAccumulator(int capacity) {
        return new LongAccumulator(capacity);
    }
}
