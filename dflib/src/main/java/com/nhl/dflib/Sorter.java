package com.nhl.dflib;

import com.nhl.dflib.sort.IntComparator;

/**
 * An expression for sorting DataFrame data.
 *
 * @since 0.11
 */
public interface Sorter {

    IntComparator eval(DataFrame df);
}
