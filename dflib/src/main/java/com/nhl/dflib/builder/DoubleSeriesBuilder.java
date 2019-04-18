package com.nhl.dflib.builder;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.DoubleMutableList;
import com.nhl.dflib.map.DoubleValueMapper;

/**
 * @param <I>
 * @since 0.6
 */
public class DoubleSeriesBuilder<I> implements SeriesBuilder<I, Double> {

    private DoubleMutableList accumulator;
    private DoubleValueMapper<I> mapper;

    public DoubleSeriesBuilder(DoubleValueMapper<I> mapper) {
        this.accumulator = new DoubleMutableList();
        this.mapper = mapper;
    }

    @Override
    public void append(I v) {
        accumulator.add(mapper.map(v));
    }

    @Override
    public Series<Double> toSeries() {
        return accumulator.toDoubleSeries();
    }
}
