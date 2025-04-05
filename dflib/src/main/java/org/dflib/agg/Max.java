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

        // "0." will be returned if size == 0 or all series values are nulls
        double max = 0.;
        int i;

        // init "max" with the first non-null value
        for (i = 0; i < size; i++) {
            Number n = s.get(i);
            if (n != null) {
                max = n.doubleValue();
                break;
            }
        }

        // now find the "max"
        for (; i < size; i++) {

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

        // "0f" will be returned if size == 0 or all series values are nulls
        float max = 0f;
        int i;

        // init "max" with the first non-null value
        for (i = 0; i < size; i++) {
            Number n = s.get(i);
            if (n != null) {
                max = n.floatValue();
                break;
            }
        }

        // now find the "max"
        for (; i < size; i++) {

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

        // "0" will be returned if size == 0 or all series values are nulls
        int max = 0;
        int i;

        // init "max" with the first non-null value
        for (i = 0; i < size; i++) {
            Number n = s.get(i);
            if (n != null) {
                max = n.intValue();
                break;
            }
        }

        // now find the "max"
        for (; i < size; i++) {

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

        // "0L" will be returned if size == 0 or all series values are nulls
        long max = 0L;
        int i;

        // init "max" with the first non-null value
        for (i = 0; i < size; i++) {
            Number n = s.get(i);
            if (n != null) {
                max = n.longValue();
                break;
            }
        }

        // now find the "max"
        for (; i < size; i++) {

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
