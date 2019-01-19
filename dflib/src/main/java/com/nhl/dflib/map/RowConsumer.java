package com.nhl.dflib.map;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface RowConsumer {

    void consume(Index columns, Object[] row);
}
