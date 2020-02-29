package com.nhl.dflib.series.builder;

/**
 * Performs type conversion and passes the converted result to a special holder. This allows to do conversions
 * without "unboxing" the result, and thus can be used for either objects or primitives in a uniform way.
 *
 * @since 0.8
 */
public interface ValueConverter<V, T> {

    void convertAndStore(V v, ValueHolder<T> holder);

    void convertAndStore(V v, Accumulator<T> accumulator);

    void convertAndStore(int pos, V v, Accumulator<T> accumulator);

}
