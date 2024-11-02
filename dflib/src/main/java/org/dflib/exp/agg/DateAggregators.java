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

    public static LocalDate median(Series<LocalDate> s) {

        int size = s.size();

        switch (size) {
            case 0:
                return null;
            case 1:
                return s.get(0);
            default:

                Series<LocalDate> sorted = s.select(DateTimeAggregators.notNullExp).sort(DateTimeAggregators.asc);

                int nonNullSize = sorted.size();
                int m = nonNullSize / 2;

                int odd = nonNullSize % 2;
                if (odd == 1) {
                    return sorted.get(m);
                }

                LocalDate d1 = sorted.get(m - 1);
                LocalDate d2 = sorted.get(m);
                return d1.plusDays(Math.round(ChronoUnit.DAYS.between(d1, d2) / 2.));
        }
    }
}
