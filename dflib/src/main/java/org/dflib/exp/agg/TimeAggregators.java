package org.dflib.exp.agg;

import org.dflib.Series;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * @since 2.0.0
 */
public class TimeAggregators {

    public static LocalTime avg(Series<LocalTime> s) {
        int size = s.size();

        switch (size) {
            case 0:
                return null;
            case 1:
                return s.get(0);
            default:

                long nanos = 0;
                LocalTime first = s.first();

                for (int i = 1; i < size; i++) {
                    nanos += ChronoUnit.NANOS.between(first, s.get(i));
                }

                return first.plusNanos(Math.round(nanos / (double) size));
        }
    }
}
