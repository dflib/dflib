package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.series.builder.ObjectAccumulator;

/**
 * @param <I>
 * @param <O>
 * @since 0.8
 */
public class ObjectSeriesBuilder<I, O> implements SeriesBuilder<I, O> {

    private ValueMapper<I, O> mapper;
    private ObjectAccumulator<O> accumulator;

    public ObjectSeriesBuilder(ValueMapper<I, O> mapper) {
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
    public Series<O> toSeries() {
        return accumulator.toSeries();
    }
}
