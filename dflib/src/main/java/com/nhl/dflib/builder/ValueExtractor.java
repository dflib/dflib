package com.nhl.dflib.builder;

/**
 * Extracts a value from a source object and passes the result to an abstract value "store". This allows conversions
 * without "unboxing" the result, and thus can be used for either objects or primitives in a uniform way. Extractors
 * are themselves immutable, but can help users to create mutable stores ({@link ValueAccum} and {@link ValueHolder})
 * that match the extractor primitive type.
 *
 * @since 0.16
 */
public interface ValueExtractor<F, T> {

    void extract(F from, ValueStore<T> to);

    void extract(F from, ValueStore<T> to, int toPos);

    default ValueAccum<T> createAccum() {
        return createAccum(10);
    }

    ValueAccum<T> createAccum(int capacity);

    ValueHolder<T> createHolder();
}
