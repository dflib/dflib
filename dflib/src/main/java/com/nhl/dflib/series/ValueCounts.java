package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

class ValueCounts {


    public static <T> DataFrame valueCountsNoNulls(Series<T> series) {
        return series.group()
                .aggMultiple(
                        Exp.$col("").first().as("value"),
                        Exp.count().as("count")
                )
                .sort(1, false);
    }

    public static DataFrame valueCountsMaybeNulls(Series<?> series) {
        return series.select(series.index(v -> v != null))
                .group()
                .aggMultiple(
                        Exp.$col("").first().as("value"),
                        Exp.count().as("count")
                )
                .sort(1, false);
    }
}
