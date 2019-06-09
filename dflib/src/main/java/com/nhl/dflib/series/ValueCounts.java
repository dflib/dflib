package com.nhl.dflib.series;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

class ValueCounts {


    public static DataFrame valueCountsNoNulls(Series<?> series) {
        return DataFrame
                .newFrame("x")
                .columns(series)
                .group(0)
                .agg(
                        Aggregator.first(0).named("value"),
                        Aggregator.countInt().named("count")
                )
                .sort(1, false);
    }

    public static DataFrame valueCountsMaybeNulls(Series<?> series) {
        Series<?> sansNull = series.select(series.index(v -> v != null));
        return DataFrame
                .newFrame("x")
                .columns(sansNull)
                .group(0)
                .agg(
                        Aggregator.first(0).named("value"),
                        Aggregator.countInt().named("count")
                )
                .sort(1, false);
    }
}
