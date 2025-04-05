package org.dflib.exp.agg;

import org.dflib.DoubleSeries;
import org.dflib.Series;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.builder.ObjectAccum;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class DoubleAggregators {

    private static final Function<Series<? extends Number>, Double> avg =
            CollectorAggregator.create((Collector) Collectors.averagingDouble(Number::doubleValue));
    private static final Function<Series<? extends Number>, Double> sum =
            CollectorAggregator.create((Collector) Collectors.summingDouble(Number::doubleValue));


    public static Series<Double> cumSum(Series<? extends Number> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofDouble();
        }

        if (s instanceof DoubleSeries) {
            return ((DoubleSeries) s).cumSum();
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
     * @deprecated use {@link org.dflib.agg.Min#ofDoubles(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double min(Series<? extends Number> s) {
        return Min.ofDoubles(s);
    }

    /**
     * @deprecated use {@link org.dflib.agg.Max#ofDoubles(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double max(Series<? extends Number> s) {
        return Max.ofDoubles(s);
    }

    public static double avg(Series<? extends Number> s) {
        return s.size() == 0 ? 0. : avg.apply(s);
    }

    /**
     * @deprecated in favor of {@link Percentiles#ofDoubles(Series, double)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static double median(Series<? extends Number> s) {
        return Percentiles.ofDoubles(s, 0.5);
    }
}
