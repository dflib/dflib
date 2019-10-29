package com.nhl.dflib.aggregate;

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
}
