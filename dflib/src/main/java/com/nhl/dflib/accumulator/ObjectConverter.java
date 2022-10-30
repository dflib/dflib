package com.nhl.dflib.accumulator;

import com.nhl.dflib.ValueMapper;

/**
 * @since 0.8
 */
public class ObjectConverter<F, T> implements ValueConverter<F, T> {

    private final ValueMapper<F, T> converter;

    public ObjectConverter(ValueMapper<F, T> converter) {
        this.converter = converter;
    }

    @Override
    public void convertAndStore(F v, ValueHolder<T> holder) {
        holder.set(converter.map(v));
    }

    @Override
    public void convertAndStore(F v, Accumulator<T> accumulator) {
        accumulator.add(converter.map(v));
    }

    @Override
    public void convertAndStore(int pos, F v, Accumulator<T> accumulator) {
        accumulator.set(pos, converter.map(v));
    }
}
