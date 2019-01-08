package com.nhl.dflib.map;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface DataRowToValueMapper<V> {

    V map(Index c, Object[] row);
}
