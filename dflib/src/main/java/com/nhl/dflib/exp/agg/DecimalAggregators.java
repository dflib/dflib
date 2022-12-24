package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.builder.ObjectAccum;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @since 0.11
 */
public class DecimalAggregators {

    private static final Condition notNullExp = Exp.$decimal(0).isNotNull();
    private static final Sorter asc = Exp.$decimal(0).asc();

    /**
     * @since 0.14
     */
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

    public static BigDecimal median(Series<BigDecimal> s) {

        int size = s.size();

        switch (size) {
            case 0:
                return BigDecimal.ZERO;
            case 1:
                BigDecimal d = s.get(0);
                return d != null ? d : BigDecimal.ZERO;
            default:

                Series<BigDecimal> sorted = s.select(notNullExp).sort(asc);

                int nonNullSize = sorted.size();
                int m = nonNullSize / 2;

                int odd = nonNullSize % 2;
                if (odd == 1) {
                    return sorted.get(m);
                }

                BigDecimal d1 = sorted.get(m - 1);
                BigDecimal d2 = sorted.get(m);
                return d2.subtract(d1).divide(new BigDecimal("2.0"), RoundingMode.HALF_UP).add(d1);
        }
    }
}
