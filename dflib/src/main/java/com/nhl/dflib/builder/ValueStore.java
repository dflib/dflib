package com.nhl.dflib.builder;

/**
 * An abstraction of a mutable primitives-aware store of values.
 *
 * @since 0.16
 */
public interface ValueStore<T> {

    default void push(T v) {
        throw new UnsupportedOperationException("This Sink does not support pushing values");
    }

    default void replace(int pos, T v) {
        throw new UnsupportedOperationException("This Sink does not support setting positional values");
    }

    default void pushBoolean(boolean v) {
        throw new UnsupportedOperationException("This Sink does not support 'boolean'");
    }

    default void replaceBoolean(int pos, boolean v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'boolean'");
    }

    default void pushInt(int v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'int'");
    }

    default void replaceInt(int pos, int v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'int'");
    }

    default void pushLong(long v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'long'");
    }

    default void replaceLong(int pos, long v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'long'");
    }

    default void pushDouble(double v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'double'");
    }

    default void replaceDouble(int pos, double v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'double'");
    }
}
