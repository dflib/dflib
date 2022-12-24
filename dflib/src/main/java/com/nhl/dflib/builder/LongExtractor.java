package com.nhl.dflib.builder;

import com.nhl.dflib.LongValueMapper;

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
    public ValueAccum<Long> createAccum(int capacity) {
        return new LongAccum(capacity);
    }

    @Override
    public ValueHolder<Long> createHolder() {
        return new LongHolder();
    }
}
