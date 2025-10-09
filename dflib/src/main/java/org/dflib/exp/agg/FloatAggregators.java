package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.agg.Average;
import org.dflib.agg.CumSum;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.agg.Sum;

/**
 * @since 1.1.0
 * @deprecated in favor of individual aggregating operations like {@link Sum}, {@link Min}, etc.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class FloatAggregators {

    /**
     * @deprecated in favor of {@link CumSum#ofFloats(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Series<Double> cumSum(Series<? extends Number> s) {
        return CumSum.ofFloats(s);
    }

    /**
     * @deprecated in favor of {@link Sum#ofFloats(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double sum(Series<? extends Number> s) {
        return Sum.ofFloats(s);
    }

    /**
     * @deprecated in favor of {@link Min#ofFloats(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static float min(Series<? extends Number> s) {
        return Min.ofFloats(s);
    }

    /**
     * @deprecated in favor of {@link Max#ofFloats(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static float max(Series<? extends Number> s) {
        return Max.ofFloats(s);
    }

    /**
     * @deprecated in favor of {@link Average#ofFloats(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static float avg(Series<? extends Number> s) {
        return Average.ofFloats(s);
    }

    /**
     * @deprecated in favor of {@link Percentiles#ofFloats(Series, double)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static float median(Series<? extends Number> s) {
        return Percentiles.ofFloats(s, 0.5);
    }
}
