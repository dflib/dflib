package org.dflib.exp.agg;

import org.dflib.Series;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @since 2.0.0
 */
public class DateAggregators {

    public static LocalDate avg(Series<LocalDate> s) {
        int size = s.size();

        switch (size) {
            case 0:
                return null;
            case 1:
                return s.get(0);
            default:

                long days = 0;
                LocalDate first = s.first();

                for (int i = 1; i < size; i++) {
                    days += ChronoUnit.DAYS.between(first, s.get(i));
                }

                return first.plusDays(Math.round(days / (double) size));
        }
    }
}
