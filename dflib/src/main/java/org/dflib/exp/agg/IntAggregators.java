package org.dflib.exp.agg;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.builder.ObjectAccum;


public class IntAggregators {


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

    /**
     * @deprecated in favor of {@link Min#ofInts(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static int min(Series<? extends Number> s) {
        return Min.ofInts(s);
    }

    /**
     * @deprecated in favor of {@link Max#ofInts(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static int max(Series<? extends Number> s) {
        return Max.ofInts(s);
    }
}
