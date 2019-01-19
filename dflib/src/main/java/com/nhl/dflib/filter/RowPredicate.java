package com.nhl.dflib.filter;

import com.nhl.dflib.Index;

@FunctionalInterface
public interface RowPredicate {

    boolean test(Index columns, Object[] r);
}
