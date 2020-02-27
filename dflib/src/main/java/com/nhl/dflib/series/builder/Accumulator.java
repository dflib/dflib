package com.nhl.dflib.series.builder;

import com.nhl.dflib.Series;

/**
 * @since 0.8
 */
public interface Accumulator<T> {
    
    void add(ValueHolder<T> valueHolder);

    void add(T v);

    default void addBoolean(boolean v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'boolean'");
    }

    default void addInt(int v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'int'");
    }

    default void addLong(long v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'long'");
    }

    default void addDouble(double v) {
        throw new UnsupportedOperationException("This Accumulator does not support 'double'");
    }

    Series<T> toSeries();
}
