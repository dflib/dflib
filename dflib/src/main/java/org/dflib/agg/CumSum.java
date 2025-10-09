package org.dflib.agg;

import org.dflib.BooleanSeries;
import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @since 2.0.0
 */
// TODO: this is not a traditional aggregating operation. Which package should it go to?
public class CumSum {

    public static Series<Long> ofInts(Series<? extends Number> s) {
        int h = s.size();
        if (h == 0) {
            return Series.ofLong();
        }

        if (s instanceof IntSeries) {
            return ((IntSeries) s).cumSum();
        }

        ObjectAccum<Long> accum = new ObjectAccum<>(h);

        int i = 0;
        long runningTotal = 0;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next.intValue();
                accum.push(runningTotal);
                i++;
                break;
            }
        }

        for (; i < h; i++) {

            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal += next.intValue();
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static Series<Long> ofLongs(Series<? extends Number> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofLong();
        }

        if (s instanceof LongSeries) {
            return ((LongSeries) s).cumSum();
        }

        ObjectAccum<Long> accum = new ObjectAccum<>(h);

        int i = 0;
        long runningTotal = 0L;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next.longValue();
                accum.push(runningTotal);
                i++;
                break;
            }
        }

        for (; i < h; i++) {

            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal += next.longValue();
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static Series<Double> ofFloats(Series<? extends Number> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofDouble();
        }

        if (s instanceof FloatSeries) {
            return ((FloatSeries) s).cumSum();
        }

        ObjectAccum<Double> accum = new ObjectAccum<>(h);

        int i = 0;
        double runningTotal = 0.;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next.doubleValue();
                accum.push(runningTotal);
                i++;
                break;
            }
        }

        for (; i < h; i++) {

            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal += next.doubleValue();
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }


    public static Series<Double> ofDoubles(Series<? extends Number> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofDouble();
        }

        if (s instanceof DoubleSeries) {
            return ((DoubleSeries) s).cumSum();
        }

        ObjectAccum<Double> accum = new ObjectAccum<>(h);

        int i = 0;
        double runningTotal = 0.;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next.doubleValue();
                accum.push(runningTotal);
                i++;
                break;
            }
        }

        for (; i < h; i++) {

            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal += next.doubleValue();
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static Series<BigDecimal> ofDecimals(Series<BigDecimal> s) {

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

    public static Series<BigInteger> ofBigints(Series<BigInteger> s) {

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

    public static Series<Integer> ofBools(Series<Boolean> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofInt();
        }

        if (s instanceof BooleanSeries) {
            return ((BooleanSeries) s).cumSum();
        }

        ObjectAccum<Integer> accum = new ObjectAccum<>(h);

        int i = 0;
        int runningTotal = 0;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Boolean next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next ? 1 : 0;
                accum.push(runningTotal);
                i++;
                break;
            }
        }

        for (; i < h; i++) {

            Boolean next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal += next ? 1 : 0;
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static long[] ofRange(int first, int lastExclusive) {

        int len = lastExclusive - first;
        long[] cumSum = new long[len];

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += i + first;
            cumSum[i] = s;
        }

        return cumSum;
    }

    public static long[] ofArray(int[] ints, int start, int len) {

        long[] cumSum = new long[len];

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += ints[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }

    public static long[] ofArray(long[] longs, int start, int len) {

        long[] cumSum = new long[len];

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += longs[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }

    public static double[] ofArray(float[] values, int start, int len) {

        double[] cumSum = new double[len];

        double s = 0.;
        for (int i = 0; i < len; i++) {
            s += values[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }

    public static double[] ofArray(double[] doubles, int start, int len) {

        double[] cumSum = new double[len];

        double s = 0.;
        for (int i = 0; i < len; i++) {
            s += doubles[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }


    public static long[] ofValue(int val, int len) {

        long[] cumSum = new long[len];

        for (int i = 0; i < len; i++) {
            cumSum[i] = val * (i + 1);
        }

        return cumSum;
    }

    public static long[] ofValue(long val, int len) {

        long[] cumSum = new long[len];

        for (int i = 0; i < len; i++) {
            cumSum[i] = val * (i + 1);
        }

        return cumSum;
    }

    public static double[] ofValue(double val, int len) {

        // TODO: deal with rounding errors...

        double[] cumSum = new double[len];

        for (int i = 0; i < len; i++) {
            cumSum[i] = val * (i + 1);
        }

        return cumSum;
    }

    public static int[] ofArray(boolean[] bools, int start, int len) {

        int[] cumSum = new int[len];

        int s = 0;
        for (int i = 0; i < len; i++) {
            s += bools[i + start] ? 1 : 0;
            cumSum[i] = s;
        }

        return cumSum;
    }
}
