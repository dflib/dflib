package com.nhl.dflib.series.builder;

import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.Series;

/**
 * @param <I>
 * @since 0.6
 */
public class LongMappedAccumulator<I> implements SeriesBuilder<I, Long> {

    private LongAccumulator accumulator;
    private LongValueMapper<I> mapper;

    public LongMappedAccumulator(LongValueMapper<I> mapper) {
        this.accumulator = new LongAccumulator();
        this.mapper = mapper;
    }

    @Override
    public void add(I v) {
        accumulator.add(mapper.map(v));
    }

    @Override
    public Series<Long> toSeries() {
        return accumulator.toLongSeries();
    }
}
