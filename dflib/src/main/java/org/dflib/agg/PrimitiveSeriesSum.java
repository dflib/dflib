package org.dflib.agg;


/**
 * @deprecated in favor of {@link Sum} and {@link CumSum}.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class PrimitiveSeriesSum {

    public static long sumOfRange(int first, int lastExclusive) {
        return Sum.ofRange(first, lastExclusive);
    }

    public static long[] cumSumOfRange(int first, int lastExclusive) {
        return CumSum.ofRange(first, lastExclusive);
    }

    public static long[] cumSumOfArray(int[] ints, int start, int len) {
        return CumSum.ofArray(ints, start, len);
    }

    public static long[] cumSumOfValue(int val, int len) {
        return CumSum.ofValue(val, len);
    }

    public static long[] cumSumOfValue(long val, int len) {
        return CumSum.ofValue(val, len);
    }

    public static double[] cumSumOfValue(double val, int len) {
        return CumSum.ofValue(val, len);
    }

    public static long sumOfArray(int[] ints, int start, int len) {
        return Sum.ofArray(ints, start, len);
    }

    public static long[] cumSumOfArray(long[] longs, int start, int len) {
        return CumSum.ofArray(longs, start, len);
    }

    public static long sumOfArray(long[] longs, int start, int len) {
        return Sum.ofArray(longs, start, len);
    }

    /**
     * @since 1.1.0
     */
    public static double[] cumSumOfArray(float[] values, int start, int len) {
        return CumSum.ofArray(values, start, len);
    }

    public static double[] cumSumOfArray(double[] doubles, int start, int len) {
        return CumSum.ofArray(doubles, start, len);
    }

    /**
     * @since 1.1.0
     */
    public static double sumOfArray(float[] vals, int start, int len) {
        return Sum.ofArray(vals, start, len);
    }

    public static double sumOfArray(double[] doubles, int start, int len) {
        return Sum.ofArray(doubles, start, len);
    }

    /**
     * @since 1.1.0
     */
    public static int[] cumSumOfArray(boolean[] bools, int start, int len) {
        return CumSum.ofArray(bools, start, len);
    }
}
