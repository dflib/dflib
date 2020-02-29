package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.BooleanAccumulator;

/**
 * @param <I>
 * @since 0.8
 */
public class BooleanSeriesBuilder<I> implements SeriesBuilder<I, Boolean> {

    private BooleanAccumulator accumulator;
    private BooleanValueMapper<I> mapper;

    public BooleanSeriesBuilder(BooleanValueMapper<I> mapper) {
        this.accumulator = new BooleanAccumulator();
        this.mapper = mapper;
    }

    @Override
    public void add(I v) {
        accumulator.addBoolean(mapper.map(v));
    }

    @Override
    public void set(int i, I v) {
        accumulator.setBoolean(i, mapper.map(v));
    }

    @Override
    public Series<Boolean> toSeries() {
        return accumulator.toSeries();
    }
}
