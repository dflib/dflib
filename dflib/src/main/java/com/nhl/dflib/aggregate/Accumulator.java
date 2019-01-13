package com.nhl.dflib.aggregate;

@FunctionalInterface
public interface Accumulator<A, V> {

    void accumulate(A to, V v);
}
