package com.nhl.dflib.loader;

import com.nhl.dflib.DoubleValueMapper;
import com.nhl.dflib.accumulator.ValueAccum;
import com.nhl.dflib.accumulator.DoubleAccumulator;
import com.nhl.dflib.accumulator.ValueStore;

/**
 * @since 0.8
 */
public class DoubleExtractor<F> implements ValueExtractor<F, Double> {

    private final DoubleValueMapper<F> mapper;

    public DoubleExtractor(DoubleValueMapper<F> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void extract(F from, ValueStore<Double> to) {
        to.pushDouble(mapper.map(from));
    }

    @Override
    public void extract(F from, ValueStore<Double> to, int toPos) {
        to.replaceDouble(toPos, mapper.map(from));
    }

    @Override
    public ValueAccum<Double> createAccumulator(int capacity) {
        return new DoubleAccumulator(capacity);
    }
}