package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.builder.ObjectAccum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class DecimalAggregators {

    // TODO: unify with the context in the DecimalExpFactory
    private static MathContext op1Context(BigDecimal n) {
        return new MathContext(Math.max(15, 1 + n.scale()), RoundingMode.HALF_UP);
    }

    private static final Condition notNullExp = Exp.$decimal(0).isNotNull();
    private static final Sorter asc = Exp.$decimal(0).asc();

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
                return null;
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

    public static BigDecimal stdDev(Series<BigDecimal> s, boolean usePopulationStdDev) {
        BigDecimal variance = variance(s, usePopulationStdDev);
        return variance.sqrt(op1Context(variance));
    }

    // this was made public in 2.0.0
    private static final DecimalExp avg = Exp.$decimal(0).sum().div(Exp.count());

    private static BigDecimal avg(Series<BigDecimal> s) {
        return s.size() == 0 ? BigDecimal.ZERO : avg.eval(s).get(0);
    }
}
