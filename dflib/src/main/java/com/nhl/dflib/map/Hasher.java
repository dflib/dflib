package com.nhl.dflib.map;

import com.nhl.dflib.Index;

/**
 * A row "hash function" that maps a row to a value that can be used for "group by" or "hash join" operations.
 */
@FunctionalInterface
public interface Hasher {

    static Hasher forColumn(String column) {
        return (c, r) -> c.get(r, column);
    }

    static Hasher forColumn(int column) {
        return (c, r) -> c.get(r, column);
    }

    default Hasher and(String column) {
        Hasher and = Hasher.forColumn(column);
        return (c, r) -> new CombinationHash(map(c, r), and.map(c, r));
    }

    default Hasher and(int column) {
        Hasher and = Hasher.forColumn(column);
        return (c, r) -> new CombinationHash(map(c, r), and.map(c, r));
    }

    Object map(Index columns, Object[] row);
}
