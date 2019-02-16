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

    void copyRange(RowBuilder to, int fromOffset, int toOffset, int len);

    default void copyAll(RowBuilder to, int toOffset) {
        // likely hotspot - same params calculated for every row.. In internal code precalculate parameters
        // for the entire DF and use "copyRange"
        copyRange(to, 0, toOffset, Math.min(to.getIndex().span() - toOffset, getIndex().span()));
    }
}
