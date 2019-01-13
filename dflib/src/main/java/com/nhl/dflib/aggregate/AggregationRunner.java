package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;

@FunctionalInterface
public interface AggregationRunner {

    Object[] aggregate(DataFrame df);
}
