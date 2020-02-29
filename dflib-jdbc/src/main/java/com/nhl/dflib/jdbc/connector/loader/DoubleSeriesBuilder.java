package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.DoubleValueMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.DoubleAccumulator;

/**
 * @param <I>
 * @since 0.8
 */
public class DoubleSeriesBuilder<I> implements SeriesBuilder<I, Double> {

    private DoubleAccumulator accumulator;
    private DoubleValueMapper<I> mapper;

    public DoubleSeriesBuilder(DoubleValueMapper<I> mapper) {
        this.accumulator = new DoubleAccumulator();
        this.mapper = mapper;
    }

    @Override
    public void add(I v) {
        accumulator.addDouble(mapper.map(v));
    }

    @Override
    public void set(int i, I v) {
        accumulator.setDouble(i, mapper.map(v));
    }

    @Override
    public Series<Double> toSeries() {
        return accumulator.toSeries();
    }
}
