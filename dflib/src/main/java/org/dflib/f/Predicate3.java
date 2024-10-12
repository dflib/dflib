package org.dflib.f;

@FunctionalInterface
public interface Predicate3<One, Two, Three> {

    boolean test(One one, Two two, Three three);
}
