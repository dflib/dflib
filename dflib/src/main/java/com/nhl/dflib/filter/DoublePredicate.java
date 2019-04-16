package com.nhl.dflib.filter;

/**
 * @since 0.6
 */
@FunctionalInterface
public interface DoublePredicate {

    boolean test(double value);
}
