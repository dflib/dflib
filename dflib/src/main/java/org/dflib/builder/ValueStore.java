package org.dflib.builder;

/**
 * An abstraction of a mutable primitives-aware store of values.

 */
public interface ValueStore<T> {

    default void push(T v) {
        throw new UnsupportedOperationException("No support pushing values");
    }

    default void replace(int pos, T v) {
        throw new UnsupportedOperationException("No support for setting positional values");
    }

    default void pushBool(boolean v) {
        throw new UnsupportedOperationException("No support for 'boolean' values");
    }

    default void replaceBool(int pos, boolean v) {
        throw new UnsupportedOperationException("No support for 'boolean' values");
    }

    default void pushInt(int v) {
        throw new UnsupportedOperationException("No support for 'int' values");
    }

    default void replaceInt(int pos, int v) {
        throw new UnsupportedOperationException("No support for 'int' values");
    }

    default void pushLong(long v) {
        throw new UnsupportedOperationException("No support for 'long' values");
    }

    default void replaceLong(int pos, long v) {
        throw new UnsupportedOperationException("No support for 'long' values");
    }

    /**
     * @since 1.1.0
     */
    default void pushFloat(float v) {
        throw new UnsupportedOperationException("No support for 'float' values");
    }

    /**
     * @since 1.1.0
     */
    default void replaceFloat(int pos, float v) {
        throw new UnsupportedOperationException("No support for 'float' values");
    }

    default void pushDouble(double v) {
        throw new UnsupportedOperationException("No support for 'double' values");
    }

    default void replaceDouble(int pos, double v) {
        throw new UnsupportedOperationException("No support for 'double' values");
    }
}
