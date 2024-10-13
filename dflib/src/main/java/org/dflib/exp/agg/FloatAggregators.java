package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.FloatSeries;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.builder.ObjectAccum;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @since 1.1.0
 */
public class FloatAggregators {

    private static final Condition notNullExp = Exp.$col(0).isNotNull();
    private static final Sorter asc = Exp.$col(0).asc();
    private static final Function<Series<? extends Number>, Double> avg =
            CollectorAggregator.create((Collector) Collectors.averagingDouble(Number::floatValue));
    private static final Function<Series<? extends Number>, Double> sum =
            CollectorAggregator.create((Collector) Collectors.summingDouble(Number::floatValue));


    public static Series<Double> cumSum(Series<? extends Number> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofDouble();
        }

        if (s instanceof FloatSeries) {
            return ((FloatSeries) s).cumSum();
        }

        ObjectAccum<Double> accum = new ObjectAccum<>(h);

        int i = 0;
        double runningTotal = 0.;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Number next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next.doubleValue();
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
                runningTotal += next.doubleValue();
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }

    public static double sum(Series<? extends Number> s) {
        return s.size() == 0 ? 0. : sum.apply(s);
    }

    public static float min(Series<? extends Number> s) {

        int size = s.size();
        if (size == 0) {
            return 0f;
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
            return 0f;
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
        return s.size() == 0 ? 0f : avg.apply(s).floatValue();
    }

    public static float median(Series<? extends Number> s) {

        int size = s.size();

        switch (size) {
            case 0:
                return 0f;
            case 1:
                Number d = s.get(0);
                return d != null ? d.floatValue() : 0f;
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
                return d1 + (d2 - d1) / 2f;
        }
    }
}
