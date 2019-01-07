package com.nhl.yadf.filter;

@FunctionalInterface
public interface DataRowJoinPredicate {

    boolean test(Object[] lr, Object[] rr);
}
