package org.dflib.agg;

import org.dflib.Condition;
import org.dflib.DoubleSeries;
import org.dflib.Exp;
import org.dflib.FloatSeries;
import org.dflib.Series;
import org.dflib.Sorter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * @since 2.0.0
 */
public class Percentiles {

    private static final Condition notNullExp = Exp.$col(0).isNotNull();
    private static final Sorter asc = Exp.$col(0).asc();

    private static void checkIsPercentile(double quantile) {
        if (quantile < 0 || quantile > 1.0) {
            throw new IllegalArgumentException("Invalid quantile: " + quantile + ". Quantile is expected to be a percentile in the range of 0.0 .. 1.0");
        }
    }

    // TODO: should we check for edge conditions (q = 0.0, q = 1.0) and use "min" and "max" respectively?

    public static double ofRange(double quantile, int first, int lastExclusive) {

        checkIsPercentile(quantile);

        int len = lastExclusive - first;

        switch (len) {
            case 0:
                return 0;
            case 1:
                return first;
            default:
                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                return lower == upper ? first + lower : first + di;
        }
    }

    public static double ofArray(int[] vals, int start, int len, double quantile) {

        checkIsPercentile(quantile);

        switch (len) {
            case 0:
                return 0.; // is this reasonable?
            case 1:
                return vals[0];
            default:
                int[] copy = new int[len];
                System.arraycopy(vals, start, copy, 0, len);
                Arrays.sort(copy);

                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                return lower == upper
                        ? copy[lower]
                        : copy[lower] + (di - lower) * (copy[upper] - copy[lower]);
        }
    }

    public static double ofArray(long[] vals, int start, int len, double quantile) {

        checkIsPercentile(quantile);

        switch (len) {
            case 0:
                return 0.; // is this reasonable?
            case 1:
                return vals[0];
            default:
                long[] copy = new long[len];
                System.arraycopy(vals, start, copy, 0, len);
                Arrays.sort(copy);

                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                return lower == upper
                        ? copy[lower]
                        : copy[lower] + (di - lower) * (copy[upper] - copy[lower]);
        }
    }

    public static float ofArray(double quantile, int start, int len, float[] vals) {

        checkIsPercentile(quantile);

        switch (len) {
            case 0:
                return 0f; // is this reasonable?
            case 1:
                return vals[0];
            default:
                float[] copy = new float[len];
                System.arraycopy(vals, start, copy, 0, len);
                Arrays.sort(copy);

                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                return lower == upper
                        ? copy[lower]
                        : (float) (copy[lower] + (di - lower) * (copy[upper] - copy[lower]));
        }
    }

    public static double ofArray(double[] vals, int start, int len, double quantile) {

        checkIsPercentile(quantile);

        switch (len) {
            case 0:
                return 0.; // is this reasonable?
            case 1:
                return vals[0];
            default:
                double[] copy = new double[len];
                System.arraycopy(vals, start, copy, 0, len);
                Arrays.sort(copy);

                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                return lower == upper
                        ? copy[lower]
                        : copy[lower] + (di - lower) * (copy[upper] - copy[lower]);
        }
    }

    public static double ofDoubles(Series<? extends Number> s, double quantile) {
        DoubleSeries ds = (s instanceof DoubleSeries)
                ? (DoubleSeries) s
                : s.select(notNullExp).mapAsDouble(Number::doubleValue);

        return ds.quantile(quantile);
    }

    public static float ofFloats(Series<? extends Number> s, double quantile) {
        FloatSeries ps = (s instanceof FloatSeries)
                ? (FloatSeries) s
                : s.select(notNullExp).mapAsFloat(Number::floatValue);

        return ps.quantile(quantile);
    }

    public static BigDecimal ofDecimals(Series<BigDecimal> s, double quantile) {

        Percentiles.checkIsPercentile(quantile);

        Series<BigDecimal> noNulls = s.select(notNullExp);

        int len = noNulls.size();
        switch (len) {
            case 0:
                return null;
            case 1:
                return noNulls.get(0);
            default:
                Series<BigDecimal> sorted = noNulls.sort(asc);

                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                if (lower == upper) {
                    return sorted.get(lower);
                }

                BigDecimal lbd = sorted.get(lower);
                BigDecimal ubd = sorted.get(upper);
                BigDecimal fraction = new BigDecimal(di - lower);

                // sorted[lower] + (di - lower) * (sorted[upper] - sorted[lower])
                return lbd.add(ubd.subtract(lbd).multiply(fraction));
        }
    }

    public static LocalDate ofDates(Series<LocalDate> s, double quantile) {

        checkIsPercentile(quantile);

        Series<LocalDate> noNulls = s.select(notNullExp);

        int len = noNulls.size();

        switch (len) {
            case 0:
                return null;
            case 1:
                return noNulls.get(0);
            default:
                Series<LocalDate> sorted = noNulls.sort(asc);

                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                if (lower == upper) {
                    return sorted.get(lower);
                }

                LocalDate d1 = sorted.get(lower);
                LocalDate d2 = sorted.get(upper);
                double fraction = di - lower;

                return d1.plusDays(Math.round(fraction * ChronoUnit.DAYS.between(d1, d2)));
        }
    }

    public static LocalTime ofTimes(Series<LocalTime> s, double quantile) {

        checkIsPercentile(quantile);

        Series<LocalTime> noNulls = s.select(notNullExp);

        int len = noNulls.size();

        switch (len) {
            case 0:
                return null;
            case 1:
                return noNulls.get(0);
            default:
                Series<LocalTime> sorted = noNulls.sort(asc);

                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                if (lower == upper) {
                    return sorted.get(lower);
                }

                LocalTime d1 = sorted.get(lower);
                LocalTime d2 = sorted.get(upper);
                double fraction = di - lower;

                return d1.plusNanos(Math.round(fraction * ChronoUnit.NANOS.between(d1, d2)));
        }
    }

    public static LocalDateTime ofDateTimes(Series<LocalDateTime> s, double quantile) {

        checkIsPercentile(quantile);

        Series<LocalDateTime> noNulls = s.select(notNullExp);

        int len = noNulls.size();

        switch (len) {
            case 0:
                return null;
            case 1:
                return noNulls.get(0);
            default:
                Series<LocalDateTime> sorted = noNulls.sort(asc);

                double di = quantile * (len - 1);
                int lower = (int) Math.floor(di);
                int upper = (int) Math.ceil(di);

                if (lower == upper) {
                    return sorted.get(lower);
                }

                LocalDateTime d1 = sorted.get(lower);
                LocalDateTime d2 = sorted.get(upper);
                double fraction = di - lower;

                return d1.plusNanos(Math.round(fraction * ChronoUnit.NANOS.between(d1, d2)));
        }
    }
}
