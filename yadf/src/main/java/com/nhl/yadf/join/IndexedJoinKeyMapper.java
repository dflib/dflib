package com.nhl.yadf.join;

import com.nhl.yadf.Index;

@FunctionalInterface
public interface IndexedJoinKeyMapper<V> {

    V map(Index columns, Object[] row);
}
