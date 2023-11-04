package com.nhl.dflib.exp.agg;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.builder.ObjectAccum;

/**
 * @since 0.11
 */
public class IntAggregators {

    /**
     * @since 0.14
     */
    public static Series<Long> cumSum(Series<? extends Number> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofLong();
        }

        if (s instanceof IntSeries) {
            return ((IntSeries) s).cumSum();
        }

        ObjectAccum<Long> accum = new ObjectAccum<>(h);

        int i = 0;
        long runningTotal = 0;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next.intValue();
                accum.push(runningTotal);
                i++;
                break;
            }
        }

        for (; i < h; i++) {

            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal += next.intValue();
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static int sum(Series<? extends Number> s) {
        int h = s.size();
        if (h == 0) {
            return 0;
        }

        if (s instanceof IntSeries) {
            // TODO: must return long like IntSeries.sum does
            return (int) ((IntSeries) s).sum();
        }

        int sum = 0;
        for (int i = 0; i < h; i++) {
            Number n = s.get(i);

            if (n != null) {
                sum += n.intValue();
            }
        }

        // TODO: must return long like IntSeries.sum does
        return sum;
    }

    public static int min(Series<? extends Number> s) {

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

    public static int max(Series<? extends Number> s) {
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
}
