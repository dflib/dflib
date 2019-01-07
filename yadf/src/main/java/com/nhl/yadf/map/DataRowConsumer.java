package com.nhl.yadf.map;

import com.nhl.yadf.Index;

@FunctionalInterface
public interface DataRowConsumer {

    void consume(Index columns, Object[] row);
}
