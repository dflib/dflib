package org.dflib.builder;

import org.dflib.BoolValueMapper;
import org.dflib.Extractor;

/**
 * @since 1.1.0
 */
public class BitsetExtractor<F> implements Extractor<F, Boolean> {

    private final BoolValueMapper<F> mapper;

    public BitsetExtractor(BoolValueMapper<F> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void extractAndStore(F from, ValueStore<Boolean> to) {
        to.pushBool(mapper.map(from));
    }

    @Override
    public void extractAndStore(F from, ValueStore<Boolean> to, int toPos) {
        to.pushBool(mapper.map(from));
    }

    @Override
    public ValueAccum<Boolean> createAccum(int capacity) {
        return new BitsetAccum(capacity);
    }

    @Override
    public ValueHolder<Boolean> createHolder() {
        return new BoolHolder();
    }

    @Override
    public Extractor<F, Boolean> compact() {
        return this;
    }
}
