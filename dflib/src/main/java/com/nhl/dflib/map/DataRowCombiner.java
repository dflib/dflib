package com.nhl.dflib.map;

@FunctionalInterface
public interface DataRowCombiner {

    Object[] combine(CombineContext context, Object[] lr, Object[] rr);
}
