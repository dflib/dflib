package com.nhl.dflib.filter;

import com.nhl.dflib.row.RowProxy;

@FunctionalInterface
public interface RowPredicate {

    boolean test(RowProxy r);
}
