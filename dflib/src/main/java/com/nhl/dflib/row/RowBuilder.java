package com.nhl.dflib.row;

import com.nhl.dflib.Index;

/**
 * Accumulates user-provided values for a single row. Specific implementations provide their own methods to convert
 * accumulated data into an internal row representation. Just like {@link RowProxy}, {@link RowBuilder} should not be
 * cached or reused outside of the operation where it is exposed (e.g.
 * {@link com.nhl.dflib.map.RowMapper#map(RowProxy, RowBuilder)}), as its state changes between mapper calls.
 */
public interface RowBuilder {

    Index getIndex();

    void set(int columnPos, Object value);

    void set(String columnName, Object value);

    void setRange(Object[] values, int fromOffset, int toOffset, int len);

    default void setValues(Object... values) {
        setRange(values, 0, 0, getIndex().size());
    }
}

