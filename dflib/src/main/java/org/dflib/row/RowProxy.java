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


    default <T> T get(int columnPos, Class<T> type) {
        return type.cast(get(columnPos));
    }

    Object get(String columnName);


    default <T> T get(String columnName, Class<T> type) {
        return type.cast(get(columnName));
    }


    int getInt(int columnPos);


    int getInt(String columnName);


    long getLong(int columnPos);


    long getLong(String columnName);


    double getDouble(int columnPos);


    double getDouble(String columnName);


    boolean getBool(int columnPos);


    boolean getBool(String columnName);

    void copyRange(RowBuilder to, int fromOffset, int toOffset, int len);

    default void copy(RowBuilder to) {
        copyRange(to, 0, 0, Math.min(to.getIndex().size(), getIndex().size()));
    }

    default void copy(RowBuilder to, int toOffset) {
        copyRange(to, 0, toOffset, Math.min(to.getIndex().size() - toOffset, getIndex().size()));
    }
}
