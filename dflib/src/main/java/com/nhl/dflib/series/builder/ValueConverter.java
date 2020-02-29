package com.nhl.dflib.series.builder;

/**
 * An interface that allows to convert and store values with support for primitives that does not require boxing/unboxing.
 *
 * @since 0.8
 */
public interface ValueConverter<V, T> {

    void convertAndStore(V v, ValueHolder<T> holder);

    void convertAndStore(V v, Accumulator<T> accumulator);

    void convertAndStore(int pos, V v, Accumulator<T> accumulator);

}
