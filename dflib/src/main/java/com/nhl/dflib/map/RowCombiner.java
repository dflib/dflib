package com.nhl.dflib.map;

@FunctionalInterface
public interface RowCombiner {

    Object[] combine(CombineContext context, Object[] lr, Object[] rr);
}
