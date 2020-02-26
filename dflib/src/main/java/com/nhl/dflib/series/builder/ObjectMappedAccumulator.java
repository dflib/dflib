package com.nhl.dflib.series.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.ValueMapper;

/**
 * @param <I>
 * @param <O>
 * @since 0.6
 */
public class ObjectMappedAccumulator<I, O> implements SeriesBuilder<I, O> {

    private ValueMapper<I, O> mapper;
    private ObjectAccumulator<O> accumulator;

    public ObjectMappedAccumulator(ValueMapper<I, O> mapper) {
        this.mapper = mapper;
        this.accumulator = new ObjectAccumulator<>();
    }

    @Override
    public void add(I v) {
        accumulator.add(mapper.map(v));
    }

    @Override
    public void set(int pos, I v) {
        accumulator.set(pos, mapper.map(v));
    }

    @Override
    public O peek() {
        return accumulator.peek();
    }

    @Override
    public void pop() {
        accumulator.pop();
    }

    @Override
    public Series<O> toSeries() {
        return accumulator.toSeries();
    }
}
