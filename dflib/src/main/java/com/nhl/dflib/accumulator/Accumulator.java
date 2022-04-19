package com.nhl.dflib.accumulator;

import com.nhl.dflib.Series;

import java.sql.Timestamp;

/**
 * A mutable Series builder with API to create primitive and Object Series.
 *
 * @since 0.8
 */
public interface Accumulator<T> {

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

    static <T> Accumulator<?> factory(Class<T> type, int capacity) {
        if (Timestamp.class == type) {
            return new TimestampAccumulator(capacity);
        } else if (String.class == type) {
            return new StringAccumulator(capacity);
        } else if (Integer.class == type || Integer.TYPE == type || int.class == type) {
            return new IntAccumulator(capacity);
        } else if (Long.class == type || Long.TYPE == type || long.class == type) {
            return new LongAccumulator(capacity);
        } else if (Double.class == type || Double.TYPE == type || double.class == type) {
            return new DoubleAccumulator(capacity);
        } else if (Boolean.class == type || Boolean.TYPE == type || boolean.class == type) {
            return new BooleanAccumulator(capacity);
        } else {
            return new ObjectAccumulator<>(capacity);
        }
    }
}
