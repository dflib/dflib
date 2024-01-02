package org.dflib;

import org.dflib.row.RowProxy;

@FunctionalInterface
public interface RowToValueMapper<V> {

    V map(RowProxy row);
}
