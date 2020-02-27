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
    public void convertAndStore(F from, ValueHolder<Double> holder) {
        holder.setDouble(converter.map(from));
    }

    @Override
    public void convertAndStore(F from, Accumulator<Double> accumulator) {
        accumulator.addDouble(converter.map(from));
    }
}