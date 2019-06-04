package com.nhl.dflib.seriesbuilder;

import com.nhl.dflib.Series;
import com.nhl.dflib.seriesbuilder.IntAccumulator;
import com.nhl.dflib.IntValueMapper;
import com.nhl.dflib.seriesbuilder.SeriesBuilder;

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
        accumulator.add(mapper.map(v));
    }

    @Override
    public Series<Integer> toSeries() {
        return accumulator.toIntSeries();
    }
}
