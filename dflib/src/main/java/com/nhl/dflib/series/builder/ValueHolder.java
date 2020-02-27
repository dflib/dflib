package com.nhl.dflib.series.builder;

/**
 * A holder of an object or a primitive value. Use as a flyweight to provides value access API that does not require
 * boxing/unboxing of primitives.
 *
 * @since 0.8
 */
public interface ValueHolder<T> {

    T get();

    default int getInt() {
        throw new UnsupportedOperationException("Value is not an 'int'");
    }

    default void setInt(int v) {
        throw new UnsupportedOperationException("This ValueHolder does not support 'int'");
    }

    default double getDouble() {
        throw new UnsupportedOperationException("Value is not a 'double'");
    }

    default void setDouble(double v) {
        throw new UnsupportedOperationException("This ValueHolder does not support 'double'");
    }

    default long getLong() {
        throw new UnsupportedOperationException("Value is not a 'long'");
    }

    default void setLong(long v) {
        throw new UnsupportedOperationException("This ValueHolder does not support 'long'");
    }

    default boolean getBoolean() {
        throw new UnsupportedOperationException("Value is not a 'boolean'");
    }

    default void setBoolean(boolean v) {
        throw new UnsupportedOperationException("This ValueHolder does not support 'boolean'");
    }

    void set(T v);
}
