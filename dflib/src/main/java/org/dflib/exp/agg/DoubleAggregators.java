package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.agg.Average;
import org.dflib.agg.CumSum;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.agg.StandardDeviation;
import org.dflib.agg.Sum;
import org.dflib.agg.Variance;

/**
 * @deprecated in favor of individual aggregating operations like {@link Sum}, {@link Min}, etc.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class DoubleAggregators {

    /**
     * @deprecated use {@link CumSum#ofDoubles(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Series<Double> cumSum(Series<? extends Number> s) {
        return CumSum.ofDoubles(s);
    }

    /**
     * @deprecated use {@link Sum#ofDoubles(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double sum(Series<? extends Number> s) {
        return Sum.ofDoubles(s);
    }

    /**
     * @deprecated use {@link Min#ofDoubles(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double min(Series<? extends Number> s) {
        return Min.ofDoubles(s);
    }

    /**
     * @deprecated use {@link Max#ofDoubles(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double max(Series<? extends Number> s) {
        return Max.ofDoubles(s);
    }

    /**
     * @deprecated use {@link Average#ofDoubles(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double avg(Series<? extends Number> s) {
        return Average.ofDoubles(s);
    }

    /**
     * @deprecated in favor of {@link Percentiles#ofDoubles(Series, double)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double median(Series<? extends Number> s) {
        return Percentiles.ofDoubles(s, 0.5);
    }

    /**
     * @deprecated in favor of {@link Variance#ofDoubles(Series, boolean)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double variance(Series<? extends Number> s, boolean usePopulationVariance) {
        return Variance.ofDoubles(s, usePopulationVariance);
    }

    /**
     * @deprecated in favor of {@link StandardDeviation#ofDoubles(Series, boolean)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double stdDev(Series<? extends Number> s, boolean usePopulationStdDev) {
        return StandardDeviation.ofDoubles(s, usePopulationStdDev);
    }
}
