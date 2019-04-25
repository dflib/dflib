package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.LongMutableList;
import com.nhl.dflib.map.LongValueMapper;

/**
 * @param <I>
 * @since 0.6
 */
public class LongSeriesBuilder<I> implements SeriesBuilder<I, Long> {

    private LongMutableList accumulator;
    private LongValueMapper<I> mapper;

    public LongSeriesBuilder(LongValueMapper<I> mapper) {
        this.accumulator = new LongMutableList();
        this.mapper = mapper;
    }

    @Override
    public void append(I v) {
        accumulator.add(mapper.map(v));
    }

    @Override
    public Series<Long> toSeries() {
        return accumulator.toLongSeries();
    }
}
