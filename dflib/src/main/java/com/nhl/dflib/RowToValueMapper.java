package com.nhl.dflib;

import com.nhl.dflib.row.RowProxy;

@FunctionalInterface
public interface RowToValueMapper<V> {

    /**
     * @deprecated since 0.12 in favor of {@link Exp} column expressions
     */
    @Deprecated
    static <V> RowToValueMapper<V> columnReader(String column) {
        return r -> (V) r.get(column);
    }

    /**
     * @deprecated since 0.12 in favor of {@link Exp} column expressions
     */
    @Deprecated
    static <V> RowToValueMapper<V> columnReader(int column) {
        return r -> (V) r.get(column);
    }

    V map(RowProxy row);
}
