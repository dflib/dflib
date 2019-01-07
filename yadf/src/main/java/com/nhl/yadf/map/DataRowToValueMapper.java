package com.nhl.yadf.map;

@FunctionalInterface
public interface DataRowToValueMapper<V> {

    V map(MapContext c, Object[] row);
}
