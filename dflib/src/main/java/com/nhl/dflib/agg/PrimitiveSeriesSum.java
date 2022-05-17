package com.nhl.dflib.agg;

/**
 * @since 0.11
 */
public class PrimitiveSeriesSum {

    public static long sumOfRange(int first, int lastExclusive) {

        long s = 0;
        for (int i = first; i < lastExclusive; i++) {
            s += i;
        }

        return s;
    }

    public static long sumOfArray(int[] ints, int start, int len) {

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += ints[i + start];
        }

        return s;
    }

    public static long sumOfArray(long[] longs, int start, int len) {

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += longs[i + start];
        }

        return s;
    }

    public static double sumOfArray(double[] doubles, int start, int len) {

        // TODO: deal with rounding errors... convert to BigDecimal?
        //   Is this method any worse than Collectors.summingDouble() as far as rounding?

        double s = 0;
        for (int i = 0; i < len; i++) {
            s += doubles[i + start];
        }

        return s;
    }
    public static float sumOfArray(float[] floats, int start, int len) {

        // TODO: deal with rounding errors... convert to BigDecimal?
        //   Is this method any worse than Collectors.summingDouble() as far as rounding?

        float s = 0;
        for (int i = 0; i < len; i++) {
            s += floats[i + start];
        }

        return s;
    }
}
