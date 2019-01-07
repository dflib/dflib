package com.nhl.dflib.join;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface IndexedJoinKeyMapper<V> {

    V map(Index columns, Object[] row);
}
