package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.IntValueMapper;
import com.nhl.dflib.series.builder.IntAccumulator;

/**
 * @param <I>
 * @since 0.8
 */
public class IntSeriesBuilder<I> implements SeriesBuilder<I, Integer> {

    private IntAccumulator accumulator;
    private IntValueMapper<I> mapper;

    public IntSeriesBuilder(IntValueMapper<I> mapper) {
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
    public Series<Integer> toSeries() {
        return accumulator.toSeries();
    }
}
