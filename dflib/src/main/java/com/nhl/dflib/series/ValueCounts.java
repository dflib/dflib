package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesAggregator;

class ValueCounts {


    public static <T> DataFrame valueCountsNoNulls(Series<T> series) {
        return series.group()
                .aggMultiple(
                        SeriesAggregator.first().named("value"),
                        SeriesAggregator.countInt().named("count")
                )
                .sort(1, false);
    }

    public static DataFrame valueCountsMaybeNulls(Series<?> series) {
        return series.select(series.index(v -> v != null))
                .group()
                .aggMultiple(
                        SeriesAggregator.first().named("value"),
                        SeriesAggregator.countInt().named("count")
                )
                .sort(1, false);
    }
}
