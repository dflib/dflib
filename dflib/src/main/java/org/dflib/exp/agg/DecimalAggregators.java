package org.dflib.exp.agg;

import org.dflib.DecimalExp;
import org.dflib.Series;
import org.dflib.agg.Percentiles;
import org.dflib.builder.ObjectAccum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.dflib.Exp.$decimal;
import static org.dflib.Exp.count;


public class DecimalAggregators {

    private static final DecimalExp avg = $decimal(0).sum().div(count());

    // TODO: unify with the context in the DecimalExpFactory
    static MathContext op1Context(BigDecimal n) {
        return new MathContext(Math.max(15, 1 + n.scale()), RoundingMode.HALF_UP);
    }

    public static Series<BigDecimal> cumSum(Series<BigDecimal> s) {

        int h = s.size();
        if (h == 0) {
            return s;
        }

        ObjectAccum<BigDecimal> accum = new ObjectAccum<>(h);

        int i = 0;
        BigDecimal runningTotal = null;

        // rewind nulls,and find the first non-null total
        for (; i < h && runningTotal == null; i++) {
            runningTotal = s.get(i);
            accum.push(runningTotal);
        }

        for (; i < h; i++) {

            BigDecimal next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = runningTotal.add(next);
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static BigDecimal sum(Series<BigDecimal> s) {

        int size = s.size();
        if (size == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = s.get(0);
        for (int i = 1; i < size; i++) {
            BigDecimal d = s.get(i);

            if (d != null) {
                sum = sum.add(d);
            }
        }

        return sum;
    }

    /**
     * @deprecated in favor of {@link Percentiles#ofDecimals(Series, double)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static BigDecimal median(Series<BigDecimal> s) {
        return Percentiles.ofDecimals(s, 0.5);
    }

    /**
     * @since 2.0.0
     */
    public static BigDecimal avg(Series<BigDecimal> s) {
        return s.size() == 0 ? BigDecimal.ZERO : avg.reduce(s);
    }

    /**
     * @since 1.3.0
     */
    public static BigDecimal variance(Series<BigDecimal> s, boolean usePopulationVariance) {

        int size = s.size();
        BigDecimal avg = DecimalAggregators.avg(s);
        MathContext mc = op1Context(avg);
        BigDecimal denominator = new BigDecimal(usePopulationVariance ? size : size - 1);

        // TODO: ignoring a possibility of nulls... e.g., numpy throws when calculating a variance of array with Nones
        //  Should we be smarter?

        BigDecimal acc = BigDecimal.ZERO;
        for (int i = 0; i < size; i++) {
            BigDecimal x = s.get(i);


            BigDecimal dev = x.subtract(avg, mc);
            BigDecimal square = dev.multiply(dev, mc);

            acc = acc.add(square, mc);
        }

        return acc.divide(denominator, mc);
    }

    /**
     * @since 1.3.0
     */
    public static BigDecimal stdDev(Series<BigDecimal> s, boolean usePopulationStdDev) {
        BigDecimal variance = variance(s, usePopulationStdDev);
        return variance.sqrt(op1Context(variance));
    }
}
