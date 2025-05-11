package org.dflib.exp.agg;

import org.dflib.FloatSeries;
import org.dflib.Series;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.builder.ObjectAccum;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @since 1.1.0
 */
public class FloatAggregators {

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

    /**
     * @deprecated in favor of {@link Min#ofFloats(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static float min(Series<? extends Number> s) {
        return Min.ofFloats(s);
    }

    /**
     * @deprecated in favor of {@link Max#ofFloats(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static float max(Series<? extends Number> s) {
        return Max.ofFloats(s);
    }

    public static float avg(Series<? extends Number> s) {
        return s.size() == 0 ? 0f : avg.apply(s).floatValue();
    }

    /**
     * @deprecated in favor of {@link Percentiles#ofFloats(Series, double)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static float median(Series<? extends Number> s) {
        return Percentiles.ofFloats(s, 0.5);
    }
}
