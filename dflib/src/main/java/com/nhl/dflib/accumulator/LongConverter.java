package com.nhl.dflib.accumulator;

import com.nhl.dflib.LongValueMapper;

/**
 * @since 0.8
 */
public class LongConverter<F> implements ValueConverter<F, Long> {

    private final LongValueMapper<F> converter;

    public LongConverter(LongValueMapper<F> converter) {
        this.converter = converter;
    }

    @Override
    public void convertAndStore(F v, ValueHolder<Long> holder) {
        holder.setLong(converter.map(v));
    }

    @Override
    public void convertAndStore(F v, Accumulator<Long> accumulator) {
        accumulator.addLong(converter.map(v));
    }

    @Override
    public void convertAndStore(int pos, F v, Accumulator<Long> accumulator) {
        accumulator.setLong(pos, converter.map(v));
    }
}
