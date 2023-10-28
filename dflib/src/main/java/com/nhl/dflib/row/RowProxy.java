package com.nhl.dflib.row;

import com.nhl.dflib.Index;

/**
 * A read-only row "proxy" provided to the outside code during DataFrame transformations and iterations. Usually the same
 * proxy points to a different row with each iteration, and hence should not be cached or relied upon outside the
 * operation scope. {@link RowProxy} is a form of a
 * <a href="https://en.wikipedia.org/wiki/Flyweight_pattern">flyweight</a>.
 */
public interface RowProxy {

    Index getIndex();

    Object get(int columnPos);

    /**
     * @since 0.19
     */
    default <T> T get(int columnPos, Class<T> type) {
        return type.cast(get(columnPos));
    }

    Object get(String columnName);

    /**
     * @since 0.19
     */
    default <T> T get(String columnName, Class<T> type) {
        return type.cast(get(columnName));
    }

    /**
     * @since 0.19
     */
    int getInt(int columnPos);

    /**
     * @since 0.19
     */
    int getInt(String columnName);

    /**
     * @since 0.19
     */
    long getLong(int columnPos);

    /**
     * @since 0.19
     */
    long getLong(String columnName);

    /**
     * @since 0.19
     */
    double getDouble(int columnPos);

    /**
     * @since 0.19
     */
    double getDouble(String columnName);

    /**
     * @since 0.19
     */
    boolean getBool(int columnPos);

    /**
     * @since 0.19
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
