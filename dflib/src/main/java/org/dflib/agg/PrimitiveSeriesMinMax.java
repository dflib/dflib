package org.dflib.agg;


/**
 * @deprecated use {@link Min} and {@link Max} classes instead.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class PrimitiveSeriesMinMax {

    public static int minOfRange(int first) {
        return Min.ofRange(first);
    }

    public static int maxOfRange(int lastExclusive) {
        return Max.ofRange(lastExclusive);
    }

    public static int minOfArray(int[] ints, int start, int len) {
        return Min.ofArray(ints, start, len);
    }

    public static int maxOfArray(int[] ints, int start, int len) {
        return Max.ofArray(ints, start, len);
    }

    public static long minOfArray(long[] longs, int start, int len) {
        return Min.ofArray(longs, start, len);
    }

    public static long maxOfArray(long[] longs, int start, int len) {
        return Max.ofArray(longs, start, len);
    }

    /**
     * @since 1.1.0
     */
    public static float minOfArray(float[] vals, int start, int len) {
        return Min.ofArray(vals, start, len);
    }

    public static double minOfArray(double[] doubles, int start, int len) {
        return Min.ofArray(doubles, start, len);
    }

    public static float maxOfArray(float[] vals, int start, int len) {
        return Max.ofArray(vals, start, len);
    }

    public static double maxOfArray(double[] doubles, int start, int len) {
        return Max.ofArray(doubles, start, len);
    }
}
