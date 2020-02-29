package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.LongAccumulator;

/**
 * @param <I>
 * @since 0.8
 */
public class LongSeriesBuilder<I> implements SeriesBuilder<I, Long> {

    private LongAccumulator accumulator;
    private LongValueMapper<I> mapper;

    public LongSeriesBuilder(LongValueMapper<I> mapper) {
        this.accumulator = new LongAccumulator();
        this.mapper = mapper;
    }

    @Override
    public void add(I v) {
        accumulator.addLong(mapper.map(v));
    }

    @Override
    public void set(int i, I v) {
        accumulator.setLong(i, mapper.map(v));
    }

    @Override
    public Series<Long> toSeries() {
        return accumulator.toSeries();
    }
}
