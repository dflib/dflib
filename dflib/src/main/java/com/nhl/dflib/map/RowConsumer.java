package com.nhl.dflib.map;

import com.nhl.dflib.row.RowProxy;

@FunctionalInterface
public interface RowConsumer {

    void consume(RowProxy row);
}
