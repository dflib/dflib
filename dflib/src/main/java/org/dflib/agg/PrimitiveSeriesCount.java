package org.dflib.agg;


public class PrimitiveSeriesCount {

    public static int countTrueInArray(boolean[] booleans, int start, int len) {

        int c = 0;
        for (int i = 0; i < len; i++) {
            if (booleans[i + start]) {
                c++;
            }
        }

        return c;
    }

    public static int countFalseInArray(boolean[] booleans, int start, int len) {

        int c = 0;
        for (int i = 0; i < len; i++) {
            if (!booleans[i + start]) {
                c++;
            }
        }

        return c;
    }
}
