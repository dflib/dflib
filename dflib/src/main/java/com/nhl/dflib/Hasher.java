package com.nhl.dflib;

import com.nhl.dflib.map.CombinationHash;
import com.nhl.dflib.row.RowProxy;

/**
 * A row "hash function" that maps a row to a value that can be used for "group by" or "hash join" operations.
 */
@FunctionalInterface
public interface Hasher {

    static Hasher forColumn(String column) {
        return r -> r.get(column);
    }

    static Hasher forColumn(int column) {
        return r -> r.get(column);
    }

    default Hasher and(String column) {
        Hasher and = Hasher.forColumn(column);
        return r -> new CombinationHash(map(r), and.map(r));
    }

    default Hasher and(int column) {
        Hasher and = Hasher.forColumn(column);
        return r -> new CombinationHash(map(r), and.map(r));
    }

    default Hasher and(Hasher hasher) {
        return r -> new CombinationHash(map(r), hasher.map(r));
    }

    Object map(RowProxy row);
}
