package com.nhl.dflib.series.builder;

/**
 * @since 0.8
 */
public interface ValueConverter<F, T> {

    void convertAndStore(F from, ValueHolder<T> holder);

    void convertAndStore(F from, Accumulator<T> accumulator);
}
