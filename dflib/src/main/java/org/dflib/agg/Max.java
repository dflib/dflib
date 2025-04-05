package org.dflib.agg;

import org.dflib.Series;

/**
 * @since 2.0.0
 */
public class Max {

    public static int ofRange(int lastExclusive) {
        return lastExclusive - 1;
    }

    public static int ofArray(int[] ints, int start, int len) {

        if (len == 0) {
            return 0; // is this reasonable?
        }

        int max = ints[start];

        for (int i = 1; i < len; i++) {

            int in = ints[start + i];
            if (in > max) {
                max = in;
            }
        }

        return max;
    }

    public static long ofArray(long[] longs, int start, int len) {

        if (len == 0) {
            return 0L; // is this reasonable?
        }

        long max = longs[start];

        for (int i = 1; i < len; i++) {

            long in = longs[start + i];
            if (in > max) {
                max = in;
            }
        }

        return max;
    }

    public static float ofArray(float[] vals, int start, int len) {

        if (len == 0) {
            return 0f; // is this reasonable?
        }

        float max = vals[start];

        for (int i = 1; i < len; i++) {

            float in = vals[start + i];
            if (in > max) {
                max = in;
            }
        }

        return max;
    }

    public static double ofArray(double[] doubles, int start, int len) {

        if (len == 0) {
            return 0.; // is this reasonable?
        }

        double max = doubles[start];

        for (int i = 1; i < len; i++) {

            double in = doubles[start + i];
            if (in > max) {
                max = in;
            }
        }

        return max;
    }

    public static double ofDoubles(Series<? extends Number> s) {
        int size = s.size();
        if (size == 0) {
            return 0.;
        }

        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < size; i++) {

            Number n = s.get(i);
            if (n != null) {
                double in = n.doubleValue();
                if (in > max) {
                    max = in;
                }
            }
        }

        return max;
    }

    public static float ofFloats(Series<? extends Number> s) {
        int size = s.size();
        if (size == 0) {
            return 0f;
        }

        float max = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < size; i++) {

            Number n = s.get(i);
            if (n != null) {
                float in = n.floatValue();
                if (in > max) {
                    max = in;
                }
            }
        }

        return max;
    }

    public static int ofInts(Series<? extends Number> s) {
        int size = s.size();
        if (size == 0) {
            return 0;
        }

        int max = Integer.MIN_VALUE;

        for (int i = 0; i < size; i++) {

            Number n = s.get(i);
            if (n != null) {
                int in = n.intValue();
                if (in > max) {
                    max = in;
                }
            }
        }

        return max;
    }

    public static long ofLongs(Series<? extends Number> s) {
        int size = s.size();
        if (size == 0) {
            return 0L;
        }

        long max = Long.MIN_VALUE;

        for (int i = 0; i < size; i++) {

            Number n = s.get(i);
            if (n != null) {
                long in = n.longValue();
                if (in > max) {
                    max = in;
                }
            }
        }

        return max;
    }
}
