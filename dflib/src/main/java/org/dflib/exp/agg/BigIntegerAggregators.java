package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.builder.ObjectAccum;

import java.math.BigInteger;

public class BigIntegerAggregators {

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
}
