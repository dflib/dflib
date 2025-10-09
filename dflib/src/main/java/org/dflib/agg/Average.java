package org.dflib.agg;

import org.dflib.DecimalExp;
import org.dflib.Series;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.dflib.Exp.*;

/**
 * @since 2.0.0
 */
public class Average {

    private static final DecimalExp bigintAvg = $bigint(0).sum().castAsDecimal().div(count());
    private static final DecimalExp decimalAvg = $decimal(0).sum().div(count());

    public static double ofRange(int first, int lastExclusive) {
        double len = lastExclusive - first;
        return Sum.ofRange(first, lastExclusive) / len;
    }

    public static double ofArray(int[] ints, int start, int len) {
        return Sum.ofArray(ints, start, len) / (double) len;
    }

    public static double ofArray(long[] longs, int start, int len) {
        // TODO: control for overflow !! We can calc averages without overflowing even if the sum can create an overflow
        return Sum.ofArray(longs, start, len) / (double) len;
    }

    public static float ofArray(float[] vals, int start, int len) {
        return (float) (Sum.ofArray(vals, start, len) / (double) len);
    }

    public static double ofArray(double[] doubles, int start, int len) {
        // TODO: control for overflow !! We can calc averages without overflowing even if the sum can create an overflow
        return Sum.ofArray(doubles, start, len) / (double) len;
    }

    public static float ofFloats(Series<? extends Number> s) {
        return SeriesCompactor.toFloatSeries(s).avg();
    }

    public static double ofDoubles(Series<? extends Number> s) {
        return SeriesCompactor.toDoubleSeries(s).avg();
    }

    public static BigDecimal ofBigints(Series<BigInteger> s) {
        // TODO: exclude nulls before the calc?
        return ofBigints_NullsNotExpected(s);
    }

    static BigDecimal ofBigints_NullsNotExpected(Series<BigInteger> s) {
        return s.size() == 0 ? BigDecimal.ZERO : bigintAvg.reduce(s);
    }

    public static BigDecimal ofDecimals(Series<BigDecimal> s) {
        // TODO: exclude nulls before the calc?
        return ofDecimals_NullsNotExpected(s);
    }

    static BigDecimal ofDecimals_NullsNotExpected(Series<BigDecimal> s) {
        return s.size() == 0 ? BigDecimal.ZERO : decimalAvg.reduce(s);
    }

    public static LocalDate ofDates(Series<LocalDate> s) {
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

    public static LocalDateTime ofDateTimes(Series<LocalDateTime> s) {
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

    public static LocalTime ofTimes(Series<LocalTime> s) {
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
