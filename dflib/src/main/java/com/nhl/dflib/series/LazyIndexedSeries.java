package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;

/**
 * A version of indexed Series that doesn't attempt to self-materialize on basic Series operations. This may be more
 * optimal than eagerly materialized {@link IndexedSeries} for short-lived columns that are transferred and thrown
 * away.
 *
 * @param <T> type of series value
 * @since 1.0.0-M19
 */
public class LazyIndexedSeries<T> extends IndexedSeries<T> {

    public LazyIndexedSeries(Series<T> source, IntSeries includePositions) {
        super(source, includePositions);
    }

    @Override
    public T get(int index) {
        Raw<T> raw = this.raw;
        return raw != null ? raw.get(index) : materialized.get(index);
    }
}
