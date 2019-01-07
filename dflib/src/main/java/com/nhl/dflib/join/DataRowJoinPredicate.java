package com.nhl.dflib.join;

@FunctionalInterface
public interface DataRowJoinPredicate {

    boolean test(JoinContext context, Object[] lr, Object[] rr);
}
