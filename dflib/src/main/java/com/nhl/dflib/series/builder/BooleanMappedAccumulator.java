package com.nhl.dflib.series.builder;

import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.Series;

/**
 * @param <I>
 * @since 0.6
 */
public class BooleanMappedAccumulator<I> implements SeriesBuilder<I, Boolean> {

    private BooleanAccumulator accumulator;
    private BooleanValueMapper<I> mapper;

    public BooleanMappedAccumulator(BooleanValueMapper<I> mapper) {
        this.accumulator = new BooleanAccumulator();
        this.mapper = mapper;
    }

    @Override
    public void add(I v) {
        accumulator.add(mapper.map(v));
    }

    @Override
    public Series<Boolean> toSeries() {
        return accumulator.toBooleanSeries();
    }
}
