package org.dflib.agg;

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

    /**
     * @since 0.14
     */
    public static long[] cumSumOfArray(int[] ints, int start, int len) {

        long[] cumSum = new long[len];

        long s = 0;
        for (int i = 0; i < len; i++) {
            s += ints[i + start];
            cumSum[i] = s;
        }

        return cumSum;
    }

    /**
     * @since 1.0.0-M19
     */
    public static long[] cumSumOfValue(int val, int len) {

        long[] cumSum = new long[len];

        for (int i = 0; i < len; i++) {
            cumSum[i] = val * (i + 1);
        }

        return cumSum;
    }

    /**
     * @since 1.0.0-M19
     */
    public static long[] cumSumOfValue(long val, int len) {

        long[] cumSum = new long[len];

        for (int i = 0; i < len; i++) {
            cumSum[i] = val * (i + 1);
        }

        return cumSum;
    }

    /**
     * @since 1.0.0-M19
     */
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

    /**
     * @since 0.14
     */
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
     * @since 0.14
     */
    public static double[] cumSumOfArray(double[] doubles, int start, int len) {

        double[] cumSum = new double[len];

        double s = 0.;
        for (int i = 0; i < len; i++) {
            s += doubles[i + start];
            cumSum[i] = s;
        }

        return cumSum;
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
}
