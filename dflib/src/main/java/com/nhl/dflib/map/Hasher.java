package com.nhl.dflib.map;

import com.nhl.dflib.Index;

/**
 * Maps a row to a key that can be used for "group by" or "indexed join" operations.
 */
@FunctionalInterface
public interface Hasher {

    static Hasher keyColumn(String column) {
        return (c, r) -> c.get(r, column);
    }

    static Hasher keyColumn(int column) {
        return (c, r) -> c.get(r, column);
    }

    default Hasher and(String column) {
        Hasher and = Hasher.keyColumn(column);
        return (c, r) -> new CombinationKey(map(c, r), and.map(c, r));
    }

    default Hasher and(int column) {
        Hasher and = Hasher.keyColumn(column);
        return (c, r) -> new CombinationKey(map(c, r), and.map(c, r));
    }

    Object map(Index columns, Object[] row);
}
