package com.nhl.dflib;

import com.nhl.dflib.row.RowProxy;

@FunctionalInterface
public interface RowToValueMapper<V> {

    V map(RowProxy row);
}
