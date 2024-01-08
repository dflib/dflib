package org.dflib.row;

import org.dflib.Index;

/**
 * Accumulates user-provided values for a single row. Specific implementations provide their own methods to convert
 * accumulated data into an internal row representation. Just like {@link RowProxy}, {@link RowBuilder} should not be
 * cached or reused outside the operation where it is exposed, as its state changes between mapper calls.
 */
public interface RowBuilder {

    Index getIndex();

    RowBuilder set(int columnPos, Object value);

    RowBuilder set(String columnName, Object value);

    RowBuilder setRange(Object[] values, int fromOffset, int toOffset, int len);

    default RowBuilder setValues(Object... values) {
        return setRange(values, 0, 0, getIndex().size());
    }
}

