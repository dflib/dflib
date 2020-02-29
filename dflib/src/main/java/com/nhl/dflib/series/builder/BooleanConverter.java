package com.nhl.dflib.series.builder;

import com.nhl.dflib.BooleanValueMapper;

/**
 * @since 0.8
 */
public class BooleanConverter <F> implements ValueConverter<F, Boolean> {

    private BooleanValueMapper<F> converter;

    public BooleanConverter(BooleanValueMapper<F> converter) {
        this.converter = converter;
    }

    @Override
    public void convertAndStore(F v, ValueHolder<Boolean> holder) {
        holder.setBoolean(converter.map(v));
    }

    @Override
    public void convertAndStore(F v, Accumulator<Boolean> accumulator) {
        accumulator.addBoolean(converter.map(v));
    }

    @Override
    public void convertAndStore(int pos, F v, Accumulator<Boolean> accumulator) {
        accumulator.setBoolean(pos, converter.map(v));
    }
}