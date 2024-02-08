package org.dflib;

import org.dflib.map.CombinationHash;
import org.dflib.row.RowProxy;

/**
 * A row "hash function" that maps a row to a value that can be used for "group by" or "hash join" operations.
 */
@FunctionalInterface
public interface Hasher {

    /**
     * @since 0.18
     */
    static Hasher of(String column) {
        return r -> r.get(column);
    }

    /**
     * @since 0.18
     */
    static Hasher of(int column) {
        return r -> r.get(column);
    }

    default Hasher and(String column) {
        Hasher and = Hasher.of(column);
        return r -> new CombinationHash(map(r), and.map(r));
    }

    default Hasher and(int column) {
        Hasher and = Hasher.of(column);
        return r -> new CombinationHash(map(r), and.map(r));
    }

    default Hasher and(Hasher hasher) {
        return r -> new CombinationHash(map(r), hasher.map(r));
    }

    Object map(RowProxy row);
}
