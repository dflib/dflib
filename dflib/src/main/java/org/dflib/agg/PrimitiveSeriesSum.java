package org.dflib.agg;


public class PrimitiveSeriesSum {

    public static long sumOfRange(int first, int lastExclusive) {

        long s = 0;
        for (int i = first; i < lastExclusive; i++) {
            s += i;
        }

        return s;
    }

    public static long[] cumSumOfRange(int first, int lastExclusive) {

        int len = lastExclusive - first;
        long[] cumSum = new long[len];

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += i + first;
            cumSum[i] = s;
        }

        return cumSum;
    }


    public static long[] cumSumOfArray(int[] ints, int start, int len) {

        long[] cumSum = new long[len];

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += ints[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }


    public static long[] cumSumOfValue(int val, int len) {

        long[] cumSum = new long[len];

        for (int i = 0; i < len; i++) {
            cumSum[i] = val * (i + 1);
        }

        return cumSum;
    }


    public static long[] cumSumOfValue(long val, int len) {

        long[] cumSum = new long[len];

        for (int i = 0; i < len; i++) {
            cumSum[i] = val * (i + 1);
        }

        return cumSum;
    }


    public static double[] cumSumOfValue(double val, int len) {

        // TODO: deal with rounding errors...

        double[] cumSum = new double[len];

        for (int i = 0; i < len; i++) {
            cumSum[i] = val * (i + 1);
        }

        return cumSum;
    }

    public static long sumOfArray(int[] ints, int start, int len) {

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += ints[i + start];
        }

        return s;
    }


    public static long[] cumSumOfArray(long[] longs, int start, int len) {

        long[] cumSum = new long[len];

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += longs[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }

    public static long sumOfArray(long[] longs, int start, int len) {

        long s = 0L;
        for (int i = 0; i < len; i++) {
            s += longs[i + start];
        }

        return s;
    }

    /**
     * @since 1.1.0
     */
    public static double[] cumSumOfArray(float[] values, int start, int len) {

        double[] cumSum = new double[len];

        double s = 0.;
        for (int i = 0; i < len; i++) {
            s += values[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }

    public static double[] cumSumOfArray(double[] doubles, int start, int len) {

        double[] cumSum = new double[len];

        double s = 0.;
        for (int i = 0; i < len; i++) {
            s += doubles[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }

    /**
     * @since 1.1.0
     */
    public static double sumOfArray(float[] vals, int start, int len) {

        // TODO: deal with rounding errors... convert to BigDecimal?
        //   Is this method any worse than Collectors.summingDouble() as far as rounding?

        double s = 0;
        for (int i = 0; i < len; i++) {
            s += vals[i + start];
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

    /**
     * @since 1.1.0
     */
    public static int[] cumSumOfArray(boolean[] bools, int start, int len) {

        int[] cumSum = new int[len];

        int s = 0;
        for (int i = 0; i < len; i++) {
            s += bools[i + start] ? 1 : 0;
            cumSum[i] = s;
        }

        return cumSum;
    }
}
