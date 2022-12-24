package com.nhl.dflib.loader;

import com.nhl.dflib.accumulator.ValueAccum;
import com.nhl.dflib.accumulator.ValueStore;

/**
 * Extracts a value from a source object and passes the result to a value "sink". This allows conversions
 * without "unboxing" the result, and thus can be used for either objects or primitives in a uniform way.
 *
 * @since 0.16
 */
public interface ValueExtractor<F, T> {

    void extract(F from, ValueStore<T> to);

    void extract(F from, ValueStore<T> to, int toPos);

    default ValueAccum<T> createAccumulator() {
        return createAccumulator(10);
    }

    /**
     * @since 0.16
     */
    ValueAccum<T> createAccumulator(int capacity);
}
