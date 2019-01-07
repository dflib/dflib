package com.nhl.yadf.filter;

import com.nhl.yadf.Index;

@FunctionalInterface
public interface DataRowPredicate {

    boolean test(Index columns, Object[] r);
}
