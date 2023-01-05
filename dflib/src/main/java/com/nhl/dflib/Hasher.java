package com.nhl.dflib;

import com.nhl.dflib.map.CombinationHash;
import com.nhl.dflib.row.RowProxy;

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

    /**
     * @deprecated since 0.18 in favor of {@link #of(String)}
     */
    @Deprecated(since = "0.18")
    static Hasher forColumn(String column) {
        return of(column);
    }

    /**
     * @deprecated since 0.18 in favor of {@link #of(int)}
     */
    @Deprecated(since = "0.18")
    static Hasher forColumn(int column) {
        return of(column);
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
