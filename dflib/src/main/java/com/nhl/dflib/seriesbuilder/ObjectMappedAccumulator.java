package com.nhl.dflib.seriesbuilder;

import com.nhl.dflib.Series;
import com.nhl.dflib.seriesbuilder.ObjectAccumulator;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.seriesbuilder.SeriesBuilder;

/**
 * @param <I>
 * @param <O>
 * @since 0.6
 */
public class ObjectMappedAccumulator<I, O> implements SeriesBuilder<I, O> {

    private ValueMapper<I, O> mapper;
    private ObjectAccumulator<O> accummulator;

    public ObjectMappedAccumulator(ValueMapper<I, O> mapper) {
        this.mapper = mapper;
        this.accummulator = new ObjectAccumulator<>();
    }

    @Override
    public void add(I v) {
        accummulator.add(mapper.map(v));
    }

    @Override
    public Series<O> toSeries() {
        return accummulator.toSeries();
    }
}
