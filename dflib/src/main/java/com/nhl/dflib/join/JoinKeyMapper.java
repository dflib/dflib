package com.nhl.dflib.join;

import com.nhl.dflib.Index;

/**
 * Maps a row to a key that can be used for indexing a join operation. The "contract" for the returned key is that its
 * "equals" method should be equivalent to a join condition. I.e. the rows on the left and the right are joined if
 * they both have equal keys. This is a faster version to join rows compared to {@link JoinPredicate}.
 *
 * @param <V>
 */
@FunctionalInterface
public interface JoinKeyMapper<V> {

    static JoinKeyMapper keyColumn(String column) {
        return (c, r) -> c.get(r, column);
    }

    static JoinKeyMapper keyColumn(int column) {
        return (c, r) -> c.get(r, column);
    }

    default JoinKeyMapper and(String column) {
        JoinKeyMapper and = JoinKeyMapper.keyColumn(column);
        return (c, r) -> new CombinationKey(map(c, r), and.map(c, r));
    }

    default JoinKeyMapper and(int column) {
        JoinKeyMapper and = JoinKeyMapper.keyColumn(column);
        return (c, r) -> new CombinationKey(map(c, r), and.map(c, r));
    }

    V map(Index columns, Object[] row);
}
