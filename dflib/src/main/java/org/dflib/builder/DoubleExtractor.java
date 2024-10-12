package org.dflib.builder;

import org.dflib.DoubleValueMapper;
import org.dflib.Extractor;

public class DoubleExtractor<F> implements Extractor<F, Double> {

    private final DoubleValueMapper<F> mapper;

    public DoubleExtractor(DoubleValueMapper<F> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Extractor<F, Double> compact() {
        return this;
    }

    @Override
    public void extractAndStore(F from, ValueStore<Double> to) {
        to.pushDouble(mapper.map(from));
    }

    @Override
    public void extractAndStore(F from, ValueStore<Double> to, int toPos) {
        to.replaceDouble(toPos, mapper.map(from));
    }

    @Override
    public ValueAccum<Double> createAccum(int capacity) {
        return new DoubleAccum(capacity);
    }

    @Override
    public ValueHolder<Double> createHolder() {
        return new DoubleHolder();
    }
}