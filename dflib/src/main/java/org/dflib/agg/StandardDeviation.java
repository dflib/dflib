package org.dflib.agg;

import org.dflib.Series;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @since 2.0.0
 */
public class StandardDeviation {

    public static double ofDoubles(Series<? extends Number> s, boolean usePopulationStdDev) {
        double variance = Variance.ofDoubles(s, usePopulationStdDev);
        return Math.sqrt(variance);
    }

    public static BigDecimal ofBigints(Series<BigInteger> s, boolean usePopulationStdDev) {
        BigDecimal variance = Variance.ofBigints(s, usePopulationStdDev);
        return variance != null ? variance.sqrt(DecimalOps.op1Context(variance)) : null;
    }

    public static BigDecimal ofDecimals(Series<BigDecimal> s, boolean usePopulationStdDev) {
        BigDecimal variance = Variance.ofDecimals(s, usePopulationStdDev);
        return variance != null ? variance.sqrt(DecimalOps.op1Context(variance)) : null;
    }
}
