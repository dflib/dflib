package com.nhl.dflib.exp.agg;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @since 0.11
 */
public class IntAggregators {

    private static final Function<Series<? extends Number>, Integer> sum =
            CollectorAggregator.create((Collector) Collectors.summingInt(Number::intValue));


    /**
     * @since 0.14
     */
    public static Series<Long> cumSum(Series<? extends Number> s) {

        int h = s.size();
        if (h == 0) {
            return LongSeries.forLongs();
        }

        if (s instanceof IntSeries) {
            return ((IntSeries) s).cumSum();
        }

        ObjectAccumulator<Long> accum = new ObjectAccumulator<>(h);

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
        return s.size() == 0 ? 0 : sum.apply(s);
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
