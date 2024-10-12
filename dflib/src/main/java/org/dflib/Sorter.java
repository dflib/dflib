package org.dflib;

import org.dflib.sort.IntComparator;

/**
 * An expression derivative for sorting Series, DataFrames and RowSets.
 */
public interface Sorter {

    IntComparator eval(DataFrame df);

    IntComparator eval(Series<?> s);
}
