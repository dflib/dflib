package com.nhl.dflib;

import com.nhl.dflib.row.RowProxy;

@FunctionalInterface
public interface RowToValueMapper<V> {

    static <V> RowToValueMapper columnReader(String column) {
        return r -> r.get(column);
    }

    static <V> RowToValueMapper columnReader(int column) {
        return r -> r.get(column);
    }

    V map(RowProxy row);
}
