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
    public void convertAndStore(F from, ValueHolder<Boolean> holder) {
        holder.setBoolean(converter.map(from));
    }

    @Override
    public void convertAndStore(F from, Accumulator<Boolean> accumulator) {
        accumulator.addBoolean(converter.map(from));
    }
}