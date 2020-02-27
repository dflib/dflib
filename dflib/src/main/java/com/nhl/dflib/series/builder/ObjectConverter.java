package com.nhl.dflib.series.builder;

import java.util.function.Function;

/**
 * @since 0.8
 */
public class ObjectConverter<F, T> implements ValueConverter<F, T> {

    private Function<F, T> converter;

    public ObjectConverter(Function<F, T> converter) {
        this.converter = converter;
    }

    @Override
    public void convertAndStore(F from, ValueHolder<T> holder) {
        holder.set(converter.apply(from));
    }

    @Override
    public void convertAndStore(F from, Accumulator<T> accumulator) {
        accumulator.add(converter.apply(from));
    }
}
