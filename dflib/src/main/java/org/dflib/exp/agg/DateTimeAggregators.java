package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.Sorter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @since 2.0.0
 */
public class DateTimeAggregators {

    static final Condition notNullExp = Exp.$col(0).isNotNull();
    static final Sorter asc = Exp.$col(0).asc();

    public static LocalDateTime avg(Series<LocalDateTime> s) {
        int size = s.size();

        switch (size) {
            case 0:
                return null;
            case 1:
                return s.get(0);
            default:

                long nanos = 0;
                LocalDateTime first = s.first();

                for (int i = 1; i < size; i++) {
                    nanos += ChronoUnit.NANOS.between(first, s.get(i));
                }

                return first.plusNanos(Math.round(nanos / (double) size));
        }
    }

    public static LocalDateTime median(Series<LocalDateTime> s) {

        int size = s.size();

        switch (size) {
            case 0:
                return null;
            case 1:
                return s.get(0);
            default:

                Series<LocalDateTime> sorted = s.select(notNullExp).sort(asc);

                int nonNullSize = sorted.size();
                int m = nonNullSize / 2;

                int odd = nonNullSize % 2;
                if (odd == 1) {
                    return sorted.get(m);
                }

                LocalDateTime dt1 = sorted.get(m - 1);
                LocalDateTime dt2 = sorted.get(m);
                return dt1.plusNanos(Math.round(ChronoUnit.NANOS.between(dt1, dt2) / 2.));
        }
    }
}
