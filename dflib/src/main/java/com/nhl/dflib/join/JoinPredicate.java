package com.nhl.dflib.join;

import com.nhl.dflib.Hasher;
import com.nhl.dflib.row.RowProxy;

/**
 * Defines a join condition for a pair of rows. A slower, but more flexible version of join condition compared to
 * {@link Hasher}.
 *
 * @see Hasher
 */
@FunctionalInterface
public interface JoinPredicate {

    boolean test(RowProxy lr, RowProxy rr);
}
