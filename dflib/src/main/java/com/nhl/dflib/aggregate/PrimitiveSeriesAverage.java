package com.nhl.dflib.aggregate;

/**
 * @since 0.7
 */
public class PrimitiveSeriesAverage {

    public static double averageOfRange(int first, int lastExclusive) {
        double len = lastExclusive - first;
        return PrimitiveSeriesSum.sumOfRange(first, lastExclusive) / len;
    }

    public static double averageOfArray(int[] ints, int start, int len) {
        return PrimitiveSeriesSum.sumOfArray(ints, start, len) / (double) len;
    }

    public static double averageOfArray(long[] longs, int start, int len) {
        return PrimitiveSeriesSum.sumOfArray(longs, start, len) / (double) len;
    }
}
