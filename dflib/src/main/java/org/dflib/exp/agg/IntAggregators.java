package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.agg.CumSum;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Sum;


/**
 * @deprecated in favor of individual aggregating operations like {@link Sum}, {@link Min}, etc.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class IntAggregators {

    /**
     * @deprecated in favor of {@link CumSum#ofInts(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Series<Long> cumSum(Series<? extends Number> s) {
        return CumSum.ofInts(s);
    }

    /**
     * @deprecated in favor of {@link Sum#ofInts(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static int sum(Series<? extends Number> s) {
        return (int) Sum.ofInts(s);
    }

    /**
     * @deprecated in favor of {@link Min#ofInts(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static int min(Series<? extends Number> s) {
        return Min.ofInts(s);
    }

    /**
     * @deprecated in favor of {@link Max#ofInts(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static int max(Series<? extends Number> s) {
        return Max.ofInts(s);
    }
}
