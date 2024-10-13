package org.dflib.builder;

import org.dflib.Extractor;
import org.dflib.FloatValueMapper;

/**
 * @since 1.1.0
 */
public class FloatExtractor<F> implements Extractor<F, Float> {

    private final FloatValueMapper<F> mapper;

    public FloatExtractor(FloatValueMapper<F> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Extractor<F, Float> compact() {
        return this;
    }

    @Override
    public void extractAndStore(F from, ValueStore<Float> to) {
        to.pushFloat(mapper.map(from));
    }

    @Override
    public void extractAndStore(F from, ValueStore<Float> to, int toPos) {
        to.replaceFloat(toPos, mapper.map(from));
    }

    @Override
    public ValueAccum<Float> createAccum(int capacity) {
        return new FloatAccum(capacity);
    }

    @Override
    public ValueHolder<Float> createHolder() {
        return new FloatHolder();
    }
}