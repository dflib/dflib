package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Series;

/**
 * @since 0.11
 */
public class ComparableAggregators {

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
}
