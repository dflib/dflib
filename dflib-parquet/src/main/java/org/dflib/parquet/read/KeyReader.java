package org.dflib.parquet.read;

import dev.hardwood.row.StructAccessor;

/**
 * Reads the raw physical value of a column, undecoded, to be used as a dictionary key. Only meaningful for the
 * fixed-width physical types, whose values fit a long; byte-array columns are keyed on their bytes instead.
 *
 * @see LongKeyDictionary
 * @since 2.0.0
 */
@FunctionalInterface
public interface KeyReader {

    long read(StructAccessor row, int fieldIndex);
}
