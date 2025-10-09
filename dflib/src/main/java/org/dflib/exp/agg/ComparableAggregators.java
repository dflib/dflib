package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.agg.Max;
import org.dflib.agg.Min;

/**
 * @deprecated replaced with {@link Min} and {@link Max} methods for Comparables.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class ComparableAggregators {

    public static <T extends Comparable<T>> T min(Series<? extends T> s) {
        return Min.ofComparables(s);
    }

    public static <T extends Comparable<T>> T max(Series<? extends T> s) {
        return Max.ofComparables(s);
    }
}
