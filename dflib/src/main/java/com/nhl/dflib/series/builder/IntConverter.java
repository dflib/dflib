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
    public void convertAndStore(F v, ValueHolder<Integer> holder) {
        holder.setInt(converter.map(v));
    }

    @Override
    public void convertAndStore(F v, Accumulator<Integer> accumulator) {
        accumulator.addInt(converter.map(v));
    }

    @Override
    public void convertAndStore(int pos, F v, Accumulator<Integer> accumulator) {
        accumulator.setInt(pos, converter.map(v));
    }
}
