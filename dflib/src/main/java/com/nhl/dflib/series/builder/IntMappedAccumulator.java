package com.nhl.dflib.series.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.IntValueMapper;

/**
 * @param <I>
 * @since 0.6
 */
public class IntMappedAccumulator<I> implements SeriesBuilder<I, Integer> {

    private IntAccumulator accumulator;
    private IntValueMapper<I> mapper;

    public IntMappedAccumulator(IntValueMapper<I> mapper) {
        this.accumulator = new IntAccumulator();
        this.mapper = mapper;
    }

    @Override
    public void add(I v) {
        accumulator.addInt(mapper.map(v));
    }

    @Override
    public void set(int i, I v) {
        accumulator.setInt(i, mapper.map(v));
    }

    @Override
    public Integer peek() {
        return accumulator.peek();
    }

    @Override
    public void pop() {
        accumulator.pop();
    }

    @Override
    public Series<Integer> toSeries() {
        return accumulator.toSeries();
    }
}
