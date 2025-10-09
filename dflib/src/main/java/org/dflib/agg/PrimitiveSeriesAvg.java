package org.dflib.agg;

/**
 * @deprecated use {@link Average} instead.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class PrimitiveSeriesAvg {

    public static double avgOfRange(int first, int lastExclusive) {
        return Average.ofRange(first, lastExclusive);
    }

    public static double avgOfArray(int[] ints, int start, int len) {
        return Average.ofArray(ints, start, len);
    }

    public static double avgOfArray(long[] longs, int start, int len) {
        return Average.ofArray(longs, start, len);
    }

    /**
     * @since 1.1.0
     */
    public static float avgOfArray(float[] vals, int start, int len) {
        return Average.ofArray(vals, start, len);
    }

    public static double avgOfArray(double[] doubles, int start, int len) {
        return Average.ofArray(doubles, start, len);
    }
}
