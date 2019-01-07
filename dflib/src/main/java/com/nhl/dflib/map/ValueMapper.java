package com.nhl.dflib.map;

@FunctionalInterface
public interface ValueMapper<V, VR> {

    VR map(V v);
}
