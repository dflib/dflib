package com.nhl.dflib.builder;

import com.nhl.dflib.BooleanValueMapper;

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
    public ValueAccum<Boolean> createAccum(int capacity) {
        return new BooleanAccum(capacity);
    }

    @Override
    public ValueHolder<Boolean> createHolder() {
        return new BooleanHolder();
    }
}