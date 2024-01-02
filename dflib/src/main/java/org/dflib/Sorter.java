package org.dflib;

import org.dflib.sort.IntComparator;

/**
 * An expression for sorting DataFrame data.
 *
 * @since 0.11
 */
public interface Sorter {

    IntComparator eval(DataFrame df);

    IntComparator eval(Series<?> s);
}
