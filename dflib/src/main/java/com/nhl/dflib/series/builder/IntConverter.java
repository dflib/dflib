package com.nhl.dflib.series.builder;

import com.nhl.dflib.IntValueMapper;

/**
 * @since 0.8
 */
public class IntConverter<F> implements ValueConverter<F, Integer> {

    private IntValueMapper<F> converter;

    public IntConverter(IntValueMapper<F> converter) {
        this.converter = converter;
    }

    @Override
    public void convertAndStore(F from, ValueHolder<Integer> holder) {
        holder.setInt(converter.map(from));
    }

    @Override
    public void convertAndStore(F from, Accumulator<Integer> accumulator) {
        accumulator.addInt(converter.map(from));
    }
}
