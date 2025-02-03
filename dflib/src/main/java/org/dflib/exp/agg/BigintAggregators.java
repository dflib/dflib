package org.dflib.exp.agg;

import org.dflib.DecimalExp;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.dflib.Exp.$bigint;
import static org.dflib.Exp.count;

/**
 * @since 2.0.0
 */
public class BigintAggregators {

    private static final DecimalExp avg = $bigint(0).sum().castAsDecimal().div(count());

    public static Series<BigInteger> cumSum(Series<BigInteger> s) {

        int h = s.size();
        if (h == 0) {
            return s;
        }

        ObjectAccum<BigInteger> accum = new ObjectAccum<>(h);

        int i = 0;
        BigInteger runningTotal = null;

        // rewind nulls,and find the first non-null total
        for (; i < h && runningTotal == null; i++) {
            runningTotal = s.get(i);
            accum.push(runningTotal);
        }

        for (; i < h; i++) {

            BigInteger next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = runningTotal.add(next);
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static BigInteger sum(Series<BigInteger> s) {

        int size = s.size();
        if (size == 0) {
            return BigInteger.ZERO;
        }

        BigInteger sum = s.get(0);
        for (int i = 1; i < size; i++) {
            BigInteger d = s.get(i);

            if (d != null) {
                sum = sum.add(d);
            }
        }

        return sum;
    }

    public static BigDecimal avg(Series<BigInteger> s) {
        return s.size() == 0 ? BigDecimal.ZERO : avg.reduce(s);
    }
}
