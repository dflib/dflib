package org.dflib.builder;

import org.dflib.BoolValueMapper;
import org.dflib.Extractor;

/**
 * @since 0.16
 */
public class BoolExtractor<F> implements Extractor<F, Boolean> {

    private final BoolValueMapper<F> mapper;

    public BoolExtractor(BoolValueMapper<F> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Extractor<F, Boolean> compact() {
        return this;
    }

    @Override
    public void extractAndStore(F from, ValueStore<Boolean> to) {
        to.pushBool(mapper.map(from));
    }

    @Override
    public void extractAndStore(F from, ValueStore<Boolean> to, int toPos) {
        to.replaceBool(toPos, mapper.map(from));
    }

    @Override
    public ValueAccum<Boolean> createAccum(int capacity) {
        return new BoolAccum(capacity);
    }

    @Override
    public ValueHolder<Boolean> createHolder() {
        return new BoolHolder();
    }
}