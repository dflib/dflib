package com.nhl.dflib.series.builder;

import com.nhl.dflib.Series;

/**
 * A mutable Series builder to create primitive and Object Series.
 *
 * @since 0.8
 */
public interface Accumulator<T> {

    void add(ValueHolder<T> valueHolder);

    void add(T v);

    void set(int pos, T v);

    default void addBoolean(boolean v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'boolean'");
    }

    default void setBoolean(int pos, boolean v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'boolean'");
    }

    default void addInt(int v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'int'");
    }

    default void setInt(int pos, int v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'int'");
    }

    default void addLong(long v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'long'");
    }

    default void setLong(int pos, long v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'long'");
    }

    default void addDouble(double v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'double'");
    }

    default void setDouble(int pos, double v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'double'");
    }

    Series<T> toSeries();
}
