package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.IntValueMapper;

/**
 * @param <I>
 * @since 0.6
 */
public class IntSeriesBuilder<I> implements SeriesBuilder<I, Integer> {

    private IntMutableList accumulator;
    private IntValueMapper<I> mapper;

    public IntSeriesBuilder(IntValueMapper<I> mapper) {
        this.accumulator = new IntMutableList();
        this.mapper = mapper;
    }

    @Override
    public void append(I v) {
        accumulator.add(mapper.map(v));
    }

    @Override
    public Series<Integer> toSeries() {
        return accumulator.toIntSeries();
    }
}
