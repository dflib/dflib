package org.dflib.row;

import org.dflib.Index;

/**
 * A read-only "proxy" that points to a single DataFrame row at any given time. Provided by the DataFrame to the outside
 * code that iterates over DataFrame rows. With each iteration, the same proxy object points to a different row. Hence,
 * it should not be accessed outside the iterator scope. {@link RowProxy} is a form of a
 * <a href="https://en.wikipedia.org/wiki/Flyweight_pattern">flyweight</a>.
 */
public interface RowProxy {

    Index getIndex();

    Object get(int columnPos);

    /**
     * @since 1.0.0-M19
     */
    default <T> T get(int columnPos, Class<T> type) {
        return type.cast(get(columnPos));
    }

    Object get(String columnName);

    /**
     * @since 1.0.0-M19
     */
    default <T> T get(String columnName, Class<T> type) {
        return type.cast(get(columnName));
    }

    /**
     * @since 1.0.0-M19
     */
    int getInt(int columnPos);

    /**
     * @since 1.0.0-M19
     */
    int getInt(String columnName);

    /**
     * @since 1.0.0-M19
     */
    long getLong(int columnPos);

    /**
     * @since 1.0.0-M19
     */
    long getLong(String columnName);

    /**
     * @since 1.0.0-M19
     */
    double getDouble(int columnPos);

    /**
     * @since 1.0.0-M19
     */
    double getDouble(String columnName);

    /**
     * @since 1.0.0-M19
     */
    boolean getBool(int columnPos);

    /**
     * @since 1.0.0-M19
     */
    boolean getBool(String columnName);

    void copyRange(RowBuilder to, int fromOffset, int toOffset, int len);

    default void copy(RowBuilder to) {
        copyRange(to, 0, 0, Math.min(to.getIndex().size(), getIndex().size()));
    }

    default void copy(RowBuilder to, int toOffset) {
        copyRange(to, 0, toOffset, Math.min(to.getIndex().size() - toOffset, getIndex().size()));
    }
}
