package org.dflib.agg;

import org.dflib.Series;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @since 2.0.0
 */
public class Sum {

    public static long ofInts(Series<? extends Number> s) {
        return SeriesCompactor.toIntSeries(s).sum();
    }

    public static long ofLongs(Series<? extends Number> s) {
        return SeriesCompactor.toLongSeries(s).sum();
    }

    public static double ofFloats(Series<? extends Number> s) {
        return SeriesCompactor.toFloatSeries(s).sum();
    }

    public static double ofDoubles(Series<? extends Number> s) {
        return SeriesCompactor.toDoubleSeries(s).sum();
    }

    public static BigDecimal ofDecimals(Series<BigDecimal> s) {

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

    public static BigInteger ofBigints(Series<BigInteger> s) {

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

    public static long ofRange(int first, int lastExclusive) {

        long s = 0;
        for (int i = first; i < lastExclusive; i++) {
            s += i;
        }

        return s;
    }

    public static long ofArray(int[] ints, int start, int len) {

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += ints[i + start];
        }

        return s;
    }

    public static long ofArray(long[] longs, int start, int len) {

        long s = 0L;
        for (int i = 0; i < len; i++) {
            s += longs[i + start];
        }

        return s;
    }

    public static double ofArray(float[] vals, int start, int len) {

        // TODO: deal with rounding errors... convert to BigDecimal?
        //   Is this method any worse than Collectors.summingDouble() as far as rounding?

        double s = 0;
        for (int i = 0; i < len; i++) {
            s += vals[i + start];
        }

        return s;
    }

    public static double ofArray(double[] doubles, int start, int len) {

        // TODO: deal with rounding errors... convert to BigDecimal?
        //   Is this method any worse than Collectors.summingDouble() as far as rounding?

        double s = 0;
        for (int i = 0; i < len; i++) {
            s += doubles[i + start];
        }

        return s;
    }
}
