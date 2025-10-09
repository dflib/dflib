package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.agg.CumSum;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.agg.StandardDeviation;
import org.dflib.agg.Sum;
import org.dflib.agg.Variance;

import java.math.BigDecimal;


/**
 * @deprecated in favor of individual aggregating operations like {@link Sum}, {@link Min}, etc.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class DecimalAggregators {

    /**
     * @deprecated in favor of {@link CumSum#ofDecimals(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Series<BigDecimal> cumSum(Series<BigDecimal> s) {
        return CumSum.ofDecimals(s);
    }

    /**
     * @deprecated in favor of {@link Sum#ofDecimals(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static BigDecimal sum(Series<BigDecimal> s) {
        return Sum.ofDecimals(s);
    }

    /**
     * @deprecated in favor of {@link Percentiles#ofDecimals(Series, double)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static BigDecimal median(Series<BigDecimal> s) {
        return Percentiles.ofDecimals(s, 0.5);
    }

    /**
     * @since 1.3.0
     * @deprecated in favor of {@link Variance#ofDecimals(Series, boolean)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static BigDecimal variance(Series<BigDecimal> s, boolean usePopulationVariance) {
        return Variance.ofDecimals(s, usePopulationVariance);
    }

    /**
     * @since 1.3.0
     * @deprecated in favor of {@link StandardDeviation#ofDecimals(Series, boolean)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static BigDecimal stdDev(Series<BigDecimal> s, boolean usePopulationStdDev) {
        return StandardDeviation.ofDecimals(s, usePopulationStdDev);
    }
}
