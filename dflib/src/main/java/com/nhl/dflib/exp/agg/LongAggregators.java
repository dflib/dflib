package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Series;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @since 0.11
 */
public class LongAggregators {

    private static final Function<Series<? extends Number>, Long> sum =
            CollectorAggregator.create((Collector) Collectors.summingLong(Number::longValue));

    public static long sum(Series<? extends Number> s) {
        return s.size() == 0 ? 0L : sum.apply(s);
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
