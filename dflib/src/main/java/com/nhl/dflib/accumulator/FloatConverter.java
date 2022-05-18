package com.nhl.dflib.accumulator;

import com.nhl.dflib.FloatValueMapper;

/**
 * @since 0.8
 */
public class FloatConverter<F> implements ValueConverter<F, Float> {

    private FloatValueMapper<F> converter;

    public FloatConverter(FloatValueMapper<F> converter) {
        this.converter = converter;
    }

    @Override
    public void convertAndStore(F v, ValueHolder<Float> holder) {
        holder.setDouble(converter.map(v));
    }

    @Override
    public void convertAndStore(F v, Accumulator<Float> accumulator) {
        accumulator.addFloat(converter.map(v));
    }

    @Override
    public void convertAndStore(int pos, F v, Accumulator<Float> accumulator) {
        accumulator.setDouble(pos, converter.map(v));
    }
}