package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.BooleanMutableList;
import com.nhl.dflib.BooleanValueMapper;

/**
 * @param <I>
 * @since 0.6
 */
public class BooleanSeriesBuilder<I> implements SeriesBuilder<I, Boolean> {

    private BooleanMutableList accumulator;
    private BooleanValueMapper<I> mapper;

    public BooleanSeriesBuilder(BooleanValueMapper<I> mapper) {
        this.accumulator = new BooleanMutableList();
        this.mapper = mapper;
    }

    @Override
    public void append(I v) {
        accumulator.add(mapper.map(v));
    }

    @Override
    public Series<Boolean> toSeries() {
        return accumulator.toBooleanSeries();
    }
}
