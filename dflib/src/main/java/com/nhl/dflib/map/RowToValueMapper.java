package com.nhl.dflib.map;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface RowToValueMapper<V> {

    static <V> RowToValueMapper columnReader(String column) {
        return (c, r) -> c.get(r, column);
    }

    static <V> RowToValueMapper columnReader(int column) {
        return (c, r) -> c.get(r, column);
    }

    V map(Index c, Object[] row);
}
