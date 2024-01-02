package org.dflib.series;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

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
