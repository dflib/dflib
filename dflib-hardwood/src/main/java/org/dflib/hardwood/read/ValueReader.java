package org.dflib.hardwood.read;

import dev.hardwood.row.StructAccessor;

/**
 * Reads a single column value out of the row currently positioned under a Hardwood {@link StructAccessor}, converting
 * it to the Java type DFLib uses for that Parquet type. Returns null for null values.
 *
 * @since 2.0.0
 */
@FunctionalInterface
public interface ValueReader<T> {

    T read(StructAccessor row, int fieldIndex);
}
