package com.nhl.dflib.map;

@FunctionalInterface
public interface DataRowToValueMapper<V> {

    V map(MapContext c, Object[] row);
}
