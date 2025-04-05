package org.dflib.exp.agg;

import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
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

    /**
     * @deprecated in favor of {@link Min#ofLongs(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static long min(Series<? extends Number> s) {
        return Min.ofLongs(s);
    }

    /**
     * @deprecated in favor of {@link Max#ofLongs(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static long max(Series<? extends Number> s) {
        return Max.ofLongs(s);
    }
}
