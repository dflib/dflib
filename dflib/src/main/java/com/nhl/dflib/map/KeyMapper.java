package com.nhl.dflib.map;

import com.nhl.dflib.Index;

/**
 * Maps a row to a key that can be used for "group by" or "indexed join" operations.
 */
@FunctionalInterface
public interface KeyMapper {

    static KeyMapper keyColumn(String column) {
        return (c, r) -> c.get(r, column);
    }

    static KeyMapper keyColumn(int column) {
        return (c, r) -> c.get(r, column);
    }

    default KeyMapper and(String column) {
        KeyMapper and = KeyMapper.keyColumn(column);
        return (c, r) -> new CombinationKey(map(c, r), and.map(c, r));
    }

    default KeyMapper and(int column) {
        KeyMapper and = KeyMapper.keyColumn(column);
        return (c, r) -> new CombinationKey(map(c, r), and.map(c, r));
    }

    Object map(Index columns, Object[] row);
}
