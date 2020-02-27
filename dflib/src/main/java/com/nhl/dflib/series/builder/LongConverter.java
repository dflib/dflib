package com.nhl.dflib.series.builder;

import com.nhl.dflib.LongValueMapper;

/**
 * @since 0.8
 */
public class LongConverter<F> implements ValueConverter<F, Long> {

    private LongValueMapper<F> converter;

    public LongConverter(LongValueMapper<F> converter) {
        this.converter = converter;
    }

    @Override
    public void convertAndStore(F from, ValueHolder<Long> holder) {
        holder.setLong(converter.map(from));
    }

    @Override
    public void convertAndStore(F from, Accumulator<Long> accumulator) {
        accumulator.addLong(converter.map(from));
    }
}
