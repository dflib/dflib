package com.nhl.dflib.filter;

/**
 * @since 0.6
 */
@FunctionalInterface
public interface LongPredicate {

    boolean test(long value);
}
