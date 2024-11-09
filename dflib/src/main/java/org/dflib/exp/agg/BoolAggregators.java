package org.dflib.exp.agg;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;

/**
 * @since 1.1.0
 */
public class BoolAggregators {

    public static Series<Integer> cumSum(Series<Boolean> s) {

        int h = s.size();
        if (h == 0) {
            return Series.ofInt();
        }

        if (s instanceof BooleanSeries) {
            return ((BooleanSeries) s).cumSum();
        }

        ObjectAccum<Integer> accum = new ObjectAccum<>(h);

        int i = 0;
        int runningTotal = 0;

        // rewind nulls,and find the first non-null total
        for (; i < h; i++) {
            Boolean next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal = next ? 1 : 0;
                accum.push(runningTotal);
                i++;
                break;
            }
        }

        for (; i < h; i++) {

            Boolean next = s.get(i);
            if (next == null) {
                accum.push(null);
            } else {
                runningTotal += next ? 1 : 0;
                accum.push(runningTotal);
            }
        }

        return accum.toSeries();
    }
}
