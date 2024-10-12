package org.dflib;

import org.dflib.row.RowBuilder;

@FunctionalInterface
public interface ValueToRowMapper<V> {

    void map(V value, RowBuilder to);
}
