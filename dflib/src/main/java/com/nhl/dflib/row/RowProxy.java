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

    Object get(String columnName);

    default void copyTo(RowBuilder to) {
        copyTo(to, 0);
    }

    void copyTo(RowBuilder to, int targetOffset);
}
