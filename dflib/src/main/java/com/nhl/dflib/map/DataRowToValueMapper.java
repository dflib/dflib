package com.nhl.dflib.map;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface DataRowToValueMapper<V> {

    static <V> DataRowToValueMapper columnReader(String column) {
        return (c, r) -> c.get(r, column);
    }

    static <V> DataRowToValueMapper columnReader(int column) {
        return (c, r) -> c.get(r, column);
    }

    V map(Index c, Object[] row);
}
