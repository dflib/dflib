package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.agg.Percentiles;
import org.dflib.builder.ObjectAccum;

import java.math.BigDecimal;


public class DecimalAggregators {

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
}
