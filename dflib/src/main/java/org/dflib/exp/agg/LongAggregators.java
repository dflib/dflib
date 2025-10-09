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
public class LongAggregators {

    /**
     * @deprecated in favor of {@link CumSum#ofLongs(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Series<Long> cumSum(Series<? extends Number> s) {
        return CumSum.ofLongs(s);
    }

    /**
     * @deprecated in favor of {@link Sum#ofLongs(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static long sum(Series<? extends Number> s) {
        return Sum.ofLongs(s);
    }

    /**
     * @deprecated in favor of {@link Min#ofLongs(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static long min(Series<? extends Number> s) {
        return Min.ofLongs(s);
    }

    /**
     * @deprecated in favor of {@link Max#ofLongs(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static long max(Series<? extends Number> s) {
        return Max.ofLongs(s);
    }
}
