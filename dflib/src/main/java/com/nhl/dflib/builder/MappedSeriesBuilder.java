package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.MutableList;
import com.nhl.dflib.map.ValueMapper;

/**
 * @param <I>
 * @param <O>
 * @since 0.6
 */
public class MappedSeriesBuilder<I, O> implements SeriesBuilder<I, O> {

    private ValueMapper<I, O> mapper;
    private MutableList<O> accummulator;

    public MappedSeriesBuilder(ValueMapper<I, O> mapper) {
        this.mapper = mapper;
        this.accummulator = new MutableList<>();
    }

    @Override
    public void append(I v) {
        accummulator.add(mapper.map(v));
    }

    @Override
    public Series<O> toSeries() {
        return accummulator.toSeries();
    }
}
