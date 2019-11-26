package com.nhl.dflib.aggregate;

import com.nhl.dflib.Series;

/**
 * @since 0.7
 */
public class SeriesMinMax {

    public static <T extends Comparable<T>> T min(Series<? extends T> s) {

        int size = s.size();
        if (size == 0) {
            return null;
        }

        T min = s.get(0);

        int i = 1;
        for (; min == null && i < size; i++) {
            min = s.get(i);
        }

        for (; i < size; i++) {

            T t = s.get(i);
            if (t != null) {
                if (t.compareTo(min) < 0) {
                    min = t;
                }
            }
        }

        return min;
    }

    public static <T extends Comparable<T>> T max(Series<? extends T> s) {

        int size = s.size();
        if (size == 0) {
            return null;
        }

        T max = s.get(0);

        int i = 1;
        for (; max == null && i < size; i++) {
            max = s.get(i);
        }

        for (; i < size; i++) {

            T t = s.get(i);
            if (t != null) {
                if (max.compareTo(t) < 0) {
                    max = t;
                }
            }
        }

        return max;
    }

    public static int maxInt(Series<? extends Number> s) {

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

    public static long maxLong(Series<? extends Number> s) {

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

    public static double maxDouble(Series<? extends Number> s) {

        int size = s.size();
        if (size == 0) {
            return 0L;
        }

        double max = Double.MIN_VALUE;

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


    public static int minInt(Series<? extends Number> s) {

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

    public static long minLong(Series<? extends Number> s) {

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

    public static double minDouble(Series<? extends Number> s) {

        int size = s.size();
        if (size == 0) {
            return 0L;
        }

        double min = Double.MAX_VALUE;

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
}
