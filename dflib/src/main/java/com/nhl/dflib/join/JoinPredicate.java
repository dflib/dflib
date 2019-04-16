package com.nhl.dflib.join;

import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.row.RowProxy;

import java.util.Objects;

/**
 * Defines a join condition for a pair of rows. A slower, but more flexible version of join condition compared to
 * {@link Hasher}.
 *
 * @see Hasher
 */
@FunctionalInterface
public interface JoinPredicate {

    /**
     * @deprecated since 0.6, as equals comparison can be handled more efficiently by the hash joins
     */
    @Deprecated
    static JoinPredicate on(String leftColumn, String rightColumn) {
        return (lr, rr) -> Objects.equals(lr.get(leftColumn), rr.get(rightColumn));
    }

    /**
     * @deprecated since 0.6, as equals comparison can be handled more efficiently by the hash joins
     */
    @Deprecated
    static JoinPredicate on(int leftColumn, int rightColumn) {
        return (lr, rr) -> Objects.equals(lr.get(leftColumn), rr.get(rightColumn));
    }

    /**
     * @deprecated since 0.6, as equals comparison can be handled more efficiently by the hash joins
     */
    @Deprecated
    default JoinPredicate and(String leftColumn, String rightColumn) {
        JoinPredicate and = JoinPredicate.on(leftColumn, rightColumn);
        return (lr, rr) -> this.test(lr, rr) && and.test(lr, rr);
    }

    /**
     * @deprecated since 0.6, as equals comparison can be handled more efficiently by the hash joins
     */
    @Deprecated
    default JoinPredicate and(int leftColumn, int rightColumn) {
        JoinPredicate and = JoinPredicate.on(leftColumn, rightColumn);
        return (lr, rr) -> this.test(lr, rr) && and.test(lr, rr);
    }

    boolean test(RowProxy lr, RowProxy rr);
}
