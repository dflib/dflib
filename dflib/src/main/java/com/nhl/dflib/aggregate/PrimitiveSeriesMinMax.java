package com.nhl.dflib.aggregate;

/**
 * @since 0.7
 */
public class PrimitiveSeriesMinMax {

    public static int minOfRange(int first) {
        return first;
    }

    public static int maxOfRange(int lastExclusive) {
        return lastExclusive - 1;
    }

    public static int minOfArray(int[] ints, int start, int len) {

        if (len == 0) {
            return 0; // is this reasonable?
        }

        int max = Integer.MAX_VALUE;

        for (int i = 0; i < len; i++) {

            int in = ints[start + i];
            if (in < max) {
                max = in;
            }
        }

        return max;
    }

    public static int maxOfArray(int[] ints, int start, int len) {

        if (len == 0) {
            return 0; // is this reasonable?
        }

        int max = Integer.MIN_VALUE;

        for (int i = 0; i < len; i++) {

            int in = ints[start + i];
            if (in > max) {
                max = in;
            }
        }

        return max;
    }
}
