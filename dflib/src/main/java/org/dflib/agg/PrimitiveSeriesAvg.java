package org.dflib.agg;


public class PrimitiveSeriesAvg {

    public static double avgOfRange(int first, int lastExclusive) {
        double len = lastExclusive - first;
        return PrimitiveSeriesSum.sumOfRange(first, lastExclusive) / len;
    }

    public static double avgOfArray(int[] ints, int start, int len) {
        return PrimitiveSeriesSum.sumOfArray(ints, start, len) / (double) len;
    }

    public static double avgOfArray(long[] longs, int start, int len) {
        // TODO: control for overflow !! We can calc averages without overflowing even if the sum can create an overflow
        return PrimitiveSeriesSum.sumOfArray(longs, start, len) / (double) len;
    }

    /**
     * @since 1.1.0
     */
    public static float avgOfArray(float[] vals, int start, int len) {
        return (float) (PrimitiveSeriesSum.sumOfArray(vals, start, len) / (double) len);
    }

    public static double avgOfArray(double[] doubles, int start, int len) {
        // TODO: control for overflow !! We can calc averages without overflowing even if the sum can create an overflow
        return PrimitiveSeriesSum.sumOfArray(doubles, start, len) / (double) len;
    }
}
