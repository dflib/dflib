package com.nhl.dflib.join;

import com.nhl.dflib.map.KeyMapper;

import java.util.Objects;

/**
 * Defines a join condition for a pair of rows. A slower, but more flexible version of join condition compared to
 * {@link KeyMapper}.
 *
 * @see KeyMapper
 */
@FunctionalInterface
public interface JoinPredicate {

    static JoinPredicate on(String leftColumn, String rightColumn) {
        return (c, lr, rr) -> Objects.equals(c.getLeft(lr, leftColumn), c.getRight(rr, rightColumn));
    }

    static JoinPredicate on(int leftColumn, int rightColumn) {
        return (c, lr, rr) -> Objects.equals(c.getLeft(lr, leftColumn), c.getRight(rr, rightColumn));
    }

    default JoinPredicate and(String leftColumn, String rightColumn) {
        JoinPredicate and = JoinPredicate.on(leftColumn, rightColumn);
        return (c, lr, rr) -> this.test(c, lr, rr) && and.test(c, lr, rr);
    }

    default JoinPredicate and(int leftColumn, int rightColumn) {
        JoinPredicate and = JoinPredicate.on(leftColumn, rightColumn);
        return (c, lr, rr) -> this.test(c, lr, rr) && and.test(c, lr, rr);
    }

    boolean test(JoinContext context, Object[] lr, Object[] rr);
}
