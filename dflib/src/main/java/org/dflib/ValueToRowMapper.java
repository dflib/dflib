package org.dflib;

import org.dflib.row.RowBuilder;

/**
 * @since 0.7
 */
@FunctionalInterface
public interface ValueToRowMapper<V> {

    void map(V value, RowBuilder to);
}
