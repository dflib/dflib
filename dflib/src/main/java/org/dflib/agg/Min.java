package org.dflib.agg;

import org.dflib.Series;

/**
 * @since 2.0.0
 */
public class Min {

    public static int ofRange(int first) {
        return first;
    }

    public static int ofArray(int[] ints, int start, int len) {

        if (len == 0) {
            return 0; // is this reasonable?
        }

        int min = ints[start];

        for (int i = 1; i < len; i++) {
            int in = ints[start + i];
            if (in < min) {
                min = in;
            }
        }

        return min;
    }

    public static long ofArray(long[] longs, int start, int len) {

        if (len == 0) {
            return 0L; // is this reasonable?
        }

        long min = longs[start];

        for (int i = 1; i < len; i++) {
            long in = longs[start + i];
            if (in < min) {
                min = in;
            }
        }

        return min;
    }

    public static float ofArray(float[] vals, int start, int len) {

        if (len == 0) {
            return 0f; // is this reasonable?
        }

        float min = vals[1];

        for (int i = start; i < len; i++) {

            float in = vals[start + i];
            if (in < min) {
                min = in;
            }
        }

        return min;
    }

    public static double ofArray(double[] doubles, int start, int len) {

        if (len == 0) {
            return 0.; // is this reasonable?
        }

        double min = doubles[start];

        for (int i = 1; i < len; i++) {

            double in = doubles[start + i];
            if (in < min) {
                min = in;
            }
        }

        return min;
    }

    public static double ofDoubles(Series<? extends Number> s) {

        int size = s.size();
        if (size == 0) {
            return 0.;
        }

        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < size; i++) {

            Number n = s.get(i);
            if (n != null) {
                double in = n.doubleValue();
                if (in < min) {
                    min = in;
                }
            }
        }

        return min;
    }

    public static float ofFloats(Series<? extends Number> s) {

        int size = s.size();
        if (size == 0) {
            return 0f;
        }

        float min = Float.POSITIVE_INFINITY;

        for (int i = 0; i < size; i++) {

            Number n = s.get(i);
            if (n != null) {
                float in = n.floatValue();
                if (in < min) {
                    min = in;
                }
            }
        }

        return min;
    }

    public static int ofInts(Series<? extends Number> s) {

        int size = s.size();
        if (size == 0) {
            return 0;
        }

        int min = Integer.MAX_VALUE;

        for (int i = 0; i < size; i++) {

            Number n = s.get(i);
            if (n != null) {
                int in = n.intValue();
                if (in < min) {
                    min = in;
                }
            }
        }

        return min;
    }

    public static long ofLongs(Series<? extends Number> s) {

        int size = s.size();
        if (size == 0) {
            return 0L;
        }

        long min = Long.MAX_VALUE;

        for (int i = 0; i < size; i++) {

            Number n = s.get(i);
            if (n != null) {
                long in = n.longValue();
                if (in < min) {
                    min = in;
                }
            }
        }

        return min;
    }
}
