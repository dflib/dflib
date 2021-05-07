package com.nhl.dflib.pivot;

import com.nhl.dflib.Series;

/**
 * Aggregator function that throws if the aggregated Series has more than one item. This prevents invalid pivot operations
 * and leads the caller to the correct API.
 */
class OneValueAggregator {

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
