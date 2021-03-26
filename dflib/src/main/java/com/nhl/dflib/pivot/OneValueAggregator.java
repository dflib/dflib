package com.nhl.dflib.pivot;

import com.nhl.dflib.Series;
import com.nhl.dflib.aggregate.SimpleSeriesAggregator;

/**
 * SeriesAggregator that throws if the aggregated Series has more than one item. This prevents invalid pivot operations
 * and leads the caller to the correct API.
 *
 * @param <T>
 */
class OneValueAggregator<T> extends SimpleSeriesAggregator<T, T> {

    public OneValueAggregator() {
        super("one_value", OneValueAggregator::getValue);
    }

    static <T> T getValue(Series<? extends T> series) {

        switch (series.size()) {
            case 0:
                throw new IllegalArgumentException("Unexpected empty Series");
            case 1:
                return series.get(0);
            default:
                throw new IllegalArgumentException(
                        "Duplicate rows in the pivot table. " +
                                "Consider passing an explicit aggregator to the pivot operation.");
        }
    }
}
