package com.nhl.dflib.filter;

@FunctionalInterface
public interface ValuePredicate<V> {

    boolean test(V value);
}
