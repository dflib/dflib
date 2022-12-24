package com.nhl.dflib.accumulator;

/**
 * Extracts a value from a source object and passes the result to a value store. This allows conversions
 * without "unboxing" the result, and thus can be used for either objects or primitives in a uniform way.
 *
 * @since 0.8
 */
public interface ValueConverter<V, T> {

    void convertAndStore(V v, ValueHolder<T> holder);

    void convertAndStore(V v, Accumulator<T> accumulator);

    void convertAndStore(int pos, V v, Accumulator<T> accumulator);

    default Accumulator<T> createAccumulator() {
        return createAccumulator(10);
    }

    /**
     * @since 0.16
     */
    Accumulator<T> createAccumulator(int capacity);
}
