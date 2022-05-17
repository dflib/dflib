package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.Sorter;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @since 0.11
 */
public class FloatAggregators {

    private static final Condition notNullExp = Exp.$col(0).isNotNull();
    private static final Sorter asc = Exp.$col(0).asc();
    private static final Function<Series<? extends Number>, Float> avg =
            CollectorAggregator.create((Collector) Collectors.averagingDouble(Number::floatValue));
    private static final Function<Series<? extends Number>, Float> sum =
            CollectorAggregator.create((Collector) Collectors.summingDouble(Number::floatValue));

    public static float sum(Series<? extends Number> s) {
        return s.size() == 0 ? 0.f : sum.apply(s);
    }

    public static float min(Series<? extends Number> s) {

        int size = s.size();
        if (size == 0) {
            return 0.f;
        }

        float min = Float.MAX_VALUE;

        for (int i = 0; i < size; i++) {

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

    public static float max(Series<? extends Number> s) {
        int size = s.size();
        if (size == 0) {
            return 0.f;
        }

        float max = Float.MIN_VALUE;

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

    public static float avg(Series<? extends Number> s) {
        return s.size() == 0 ? 0.f : avg.apply(s);
    }

    public static float median(Series<? extends Number> s) {

        int size = s.size();

        switch (size) {
            case 0:
                return 0.f;
            case 1:
                Number d = s.get(0);
                return d != null ? d.floatValue() : 0.f;
            default:

                Series<? extends Number> sorted = s.select(notNullExp).sort(asc);

                int nonNullSize = sorted.size();
                int m = nonNullSize / 2;

                int odd = nonNullSize % 2;
                if (odd == 1) {
                    return sorted.get(m).floatValue();
                }

                float d1 = sorted.get(m - 1).floatValue();
                float d2 = sorted.get(m).floatValue();
                return d1 + (d2 - d1) / 2.f;
        }
    }
}
