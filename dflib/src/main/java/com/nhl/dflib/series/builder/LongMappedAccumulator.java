package com.nhl.dflib.series.builder;

import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.Series;

/**
 * @param <I>
 * @since 0.6
 */
public class LongMappedAccumulator<I> implements SeriesBuilder<I, Long> {

    private LongAccumulator accumulator;
    private LongValueMapper<I> mapper;

    public LongMappedAccumulator(LongValueMapper<I> mapper) {
        this.accumulator = new LongAccumulator();
        this.mapper = mapper;
    }

    @Override
    public void add(I v) {
        accumulator.add(mapper.map(v));
    }

    @Override
    public void set(int i, I v) {
        accumulator.set(i, mapper.map(v));
    }

    @Override
    public Long peek() {
        return accumulator.peek();
    }

    @Override
    public void pop() {
        accumulator.pop();
    }

    @Override
    public Series<Long> toSeries() {
        return accumulator.toLongSeries();
    }
}
