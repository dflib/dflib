package com.nhl.dflib;

import com.nhl.dflib.row.RowProxy;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface RowToValueMapper<V> {

    static <V> RowToValueMapper columnReader(String column) {
        return r -> r.get(column);
    }

    static <V> RowToValueMapper columnReader(int column) {
        return r -> r.get(column);
    }

    static <V> RowToValueMapper<V> binary(RowToValueMapper<V> left, RowToValueMapper<V> right, BinaryOperator<V> op) {
        return r -> {
            V lv = left.map(r);
            if (lv == null) {
                return null;
            }

            V rv = right.map(r);
            if (rv == null) {
                return null;
            }

            return op.apply(lv, rv);
        };
    }

    V map(RowProxy row);
}
