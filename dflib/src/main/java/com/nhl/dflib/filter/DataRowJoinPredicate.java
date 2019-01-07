package com.nhl.dflib.filter;

@FunctionalInterface
public interface DataRowJoinPredicate {

    boolean test(Object[] lr, Object[] rr);
}
