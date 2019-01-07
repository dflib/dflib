package com.nhl.dflib.map;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface DataRowConsumer {

    void consume(Index columns, Object[] row);
}
