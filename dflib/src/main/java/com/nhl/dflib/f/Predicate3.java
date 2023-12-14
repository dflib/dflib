package com.nhl.dflib.f;

/**
 * @since 1.0.0-M19
 */
@FunctionalInterface
public interface Predicate3<One, Two, Three> {

    boolean test(One one, Two two, Three three);
}
