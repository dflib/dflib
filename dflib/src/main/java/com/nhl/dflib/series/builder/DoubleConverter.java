package com.nhl.dflib.series.builder;

import com.nhl.dflib.DoubleValueMapper;

/**
 * @since 0.8
 */
public class DoubleConverter<F> implements ValueConverter<F, Double> {

    private DoubleValueMapper<F> converter;

    public DoubleConverter(DoubleValueMapper<F> converter) {
        this.converter = converter;
    }

    @Override
    public void convertAndStore(F v, ValueHolder<Double> holder) {
        holder.setDouble(converter.map(v));
    }

    @Override
    public void convertAndStore(F v, Accumulator<Double> accumulator) {
        accumulator.addDouble(converter.map(v));
    }

    @Override
    public void convertAndStore(int pos, F v, Accumulator<Double> accumulator) {
        accumulator.setDouble(pos, converter.map(v));
    }
}