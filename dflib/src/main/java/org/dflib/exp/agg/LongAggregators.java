package org.dflib.exp.agg;

import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;


public class LongAggregators {


    public static Series<Long> cumSum(Series<? extends Number> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofLong();
        }

        if (s instanceof LongSeries) {
            return ((LongSeries) s).cumSum();
        }

        ObjectAccum<Long> accum = new ObjectAccum<>(h);

        int i = 0;
        long runningTotal = 0L;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next.longValue();
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
                runningTotal += next.longValue();
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static long sum(Series<? extends Number> s) {
        int h = s.size();
        if (h == 0) {
            return 0L;
        }

        if (s instanceof LongSeries) {
            return ((LongSeries) s).sum();
        }

        long sum = 0L;
        for (int i = 0; i < h; i++) {
            Number n = s.get(i);

            if (n != null) {
                sum += n.longValue();
            }
        }

        return sum;
    }

    public static long min(Series<? extends Number> s) {

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

    public static long max(Series<? extends Number> s) {
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
