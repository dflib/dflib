package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Series;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @since 0.11
 */
public class IntAggregators {

    private static final Function<Series<? extends Number>, Integer> sum =
            new CollectorAggregator(Collectors.summingInt(Number::intValue));

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
