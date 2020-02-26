package com.nhl.dflib.series.builder;

import com.nhl.dflib.DoubleValueMapper;
import com.nhl.dflib.Series;

/**
 * @param <I>
 * @since 0.6
 */
public class DoubleMappedAccumulator<I> implements SeriesBuilder<I, Double> {

    private DoubleAccumulator accumulator;
    private DoubleValueMapper<I> mapper;

    public DoubleMappedAccumulator(DoubleValueMapper<I> mapper) {
        this.accumulator = new DoubleAccumulator();
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
    public Double peek() {
        return accumulator.peek();
    }

    @Override
    public void pop() {
        accumulator.pop();
    }

    @Override
    public Series<Double> toSeries() {
        return accumulator.toDoubleSeries();
    }
}
