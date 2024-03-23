package org.dflib;

import org.dflib.sort.IntComparator;

/**
 * An expression derivative for sorting Series, DataFrames and RowSets.
 *
 * @since 0.11
 */
public interface Sorter {

    IntComparator eval(DataFrame df);

    IntComparator eval(Series<?> s);
}
