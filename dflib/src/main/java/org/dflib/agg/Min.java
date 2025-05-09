package org.dflib.agg;

import org.dflib.Series;

/**
 * @since 2.0.0
 */
public class Min {

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

        // "0." will be returned if size == 0 or all series values are nulls
        double min = 0.;
        int i;

        // init "min" with the first non-null value
        for (i = 0; i < size; i++) {
            Number n = s.get(i);
            if (n != null) {
                min = n.doubleValue();
                break;
            }
        }

        // now find the "min"
        for (; i < size; i++) {

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

        // "0f" will be returned if size == 0 or all series values are nulls
        float min = 0f;
        int i;

        // init "min" with the first non-null value
        for (i = 0; i < size; i++) {
            Number n = s.get(i);
            if (n != null) {
                min = n.floatValue();
                break;
            }
        }

        // now find the "min"
        for (; i < size; i++) {

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

        // "0" will be returned if size == 0 or all series values are nulls
        int min = 0;
        int i;

        // init "min" with the first non-null value
        for (i = 0; i < size; i++) {
            Number n = s.get(i);
            if (n != null) {
                min = n.intValue();
                break;
            }
        }

        // now find the "min"
        for (; i < size; i++) {

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

        // "0L" will be returned if size == 0 or all series values are nulls
        long min = 0L;
        int i;

        // init "min" with the first non-null value
        for (i = 0; i < size; i++) {
            Number n = s.get(i);
            if (n != null) {
                min = n.longValue();
                break;
            }
        }

        // now find the "min"
        for (; i < size; i++) {

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
