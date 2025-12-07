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
        return ofBigintsNoNullChecks(SeriesCompactor.noNullsSeries(s));
    }

    static BigDecimal ofBigintsNoNullChecks(Series<BigInteger> s) {
        return s.size() == 0 ? BigDecimal.ZERO : bigintAvg.reduce(s);
    }

    public static BigDecimal ofDecimals(Series<BigDecimal> s) {
        return ofDecimalsNoNullChecks(SeriesCompactor.noNullsSeries(s));
    }

    static BigDecimal ofDecimalsNoNullChecks(Series<BigDecimal> s) {
        return s.size() == 0 ? BigDecimal.ZERO : decimalAvg.reduce(s);
    }

    public static LocalDate ofDates(Series<LocalDate> s) {

        Series<LocalDate> noNulls = SeriesCompactor.noNullsSeries(s);
        int len = noNulls.size();

        return switch (len) {
            case 0 -> null;
            case 1 -> noNulls.get(0);
            default -> {
                long days = 0;
                LocalDate first = noNulls.first();

                for (int i = 1; i < len; i++) {
                    days += ChronoUnit.DAYS.between(first, noNulls.get(i));
                }
                yield first.plusDays(Math.round(days / (double) len));
            }
        };
    }

    public static LocalDateTime ofDateTimes(Series<LocalDateTime> s) {
        Series<LocalDateTime> noNulls = SeriesCompactor.noNullsSeries(s);

        int len = noNulls.size();

        return switch (len) {
            case 0 -> null;
            case 1 -> noNulls.get(0);
            default -> {
                long nanos = 0;
                LocalDateTime first = noNulls.first();

                for (int i = 1; i < len; i++) {
                    nanos += ChronoUnit.NANOS.between(first, noNulls.get(i));
                }

                yield first.plusNanos(Math.round(nanos / (double) len));
            }
        };
    }

    public static LocalTime ofTimes(Series<LocalTime> s) {
        Series<LocalTime> noNulls = SeriesCompactor.noNullsSeries(s);

        int len = noNulls.size();

        return switch (len) {
            case 0 -> null;
            case 1 -> noNulls.get(0);
            default -> {

                long nanos = 0;
                LocalTime first = noNulls.first();

                for (int i = 1; i < len; i++) {
                    nanos += ChronoUnit.NANOS.between(first, noNulls.get(i));
                }

                yield first.plusNanos(Math.round(nanos / (double) len));
            }
        };
    }
}
