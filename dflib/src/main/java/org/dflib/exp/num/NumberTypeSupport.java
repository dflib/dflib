package org.dflib.exp.num;

import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.dflib.exp.num.NumericExpFactory.*;

final class NumberTypeSupport {

    static final int NO_RANK = Integer.MAX_VALUE;

    private NumberTypeSupport() {
    }

    static Number castToNumber(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number n) {
            return n;
        } else {
            // note, here's a hidden validation that will throw for anything that is not-a-number
            return new BigDecimal(value.toString());
        }
    }

    static ResolvedNumExp<?> scalarExp(Number value, int rank) {
        Class<? extends Number> type = typeForRank(rank);
        return new ResolvedNumExp<>(type, Series.ofVal(value == null ? null : convert(value, rank), 1));
    }

    static Class<? extends Number> typeForRank(int rank) {
        return switch (rank) {
            case RANK_DOUBLE -> Double.class;
            case RANK_FLOAT -> Float.class;
            case RANK_BIG_INTEGER -> BigInteger.class;
            case RANK_LONG -> Long.class;
            case RANK_INT -> Integer.class;
            default -> BigDecimal.class;
        };
    }

    static NumericExpFactory factoryForRank(int rank) {
        return NumericExpFactory.factory(typeForRank(rank));
    }

    @SuppressWarnings("unchecked")
    static <N extends Number> N convert(Number number, int rank) {
        if (number == null) {
            return null;
        }

        return switch (rank) {
            case RANK_DOUBLE -> (N) Double.valueOf(number.doubleValue());
            case RANK_FLOAT -> (N) Float.valueOf(number.floatValue());
            case RANK_BIG_INTEGER -> (N) toBigInteger(number);
            case RANK_LONG -> (N) Long.valueOf(number.longValue());
            case RANK_INT -> (N) Integer.valueOf(number.intValue());
            default -> (N) toBigDecimal(number);
        };
    }

    @SuppressWarnings("unchecked")
    static <N extends Number> Series<N> convert(Series<?> series, int rank) {
        return (Series<N>) convert(series, rank, hasNulls(series));
    }

    @SuppressWarnings("unchecked")
    static Series<? extends Number> convert(Series<?> series, int rank, boolean hasNulls) {
        int knownSeriesRank = knownSeriesRank(series);
        if (rank == knownSeriesRank) {
            return (Series<? extends Number>) series;
        }

        if (rank == RANK_BIG_DECIMAL && knownSeriesRank == NO_RANK) {
            return wrapResolvedType(series.map(v -> v == null ? null : toBigDecimal(castToNumber(v))), rank);
        }

        Series<Number> numSeries = (Series<Number>) series;
        if (hasNulls) {
            return toObjectSeries(numSeries, rank);
        }

        return switch (rank) {
            case RANK_DOUBLE -> numSeries.compactDouble(Number::doubleValue);
            case RANK_FLOAT -> numSeries.compactFloat(Number::floatValue);
            case RANK_LONG -> numSeries.compactLong(Number::longValue);
            case RANK_INT -> numSeries.compactInt(Number::intValue);
            default -> toObjectSeries(numSeries, rank);
        };
    }

    static int valueRank(Number value) {
        if (value == null) {
            return NO_RANK;
        }

        return NumericExpFactory.typeConversionRank.getOrDefault(value.getClass(), RANK_BIG_DECIMAL);
    }

    static ScanResult scanSeriesIfNeeded(Series<?> series) {
        int knownRank = knownSeriesRank(series);
        return knownRank != NO_RANK
                ? new ScanResult(knownRank, series instanceof ResolvedNominalSeries<?> resolved && resolved.hasNulls())
                : scanSeries(series);
    }

    static int knownSeriesRank(Series<?> series) {
        if (series instanceof IntSeries) {
            return RANK_INT;
        } else if (series instanceof LongSeries) {
            return RANK_LONG;
        } else if (series instanceof FloatSeries) {
            return RANK_FLOAT;
        } else if (series instanceof DoubleSeries) {
            return RANK_DOUBLE;
        } else if (series instanceof ResolvedNominalSeries) {
            return NumericExpFactory.typeConversionRank.getOrDefault(series.getNominalType(), RANK_BIG_DECIMAL);
        } else {
            return NO_RANK;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static ResolvedNumExp<? extends Number> typeResolvedExp(Series<?> series, int rank, boolean hasNulls) {
        return new ResolvedNumExp(typeForRank(rank), convert(series, rank, hasNulls));
    }

    private static boolean hasNulls(Series<?> series) {
        for (int i = 0; i < series.size(); i++) {
            if (series.get(i) == null) {
                return true;
            }
        }

        return false;
    }

    private static <N extends Number> Series<N> toObjectSeries(Series<Number> series, int rank) {
        return wrapResolvedType(series.map(v -> convert(v, rank)), rank);
    }

    private static <N extends Number> Series<N> wrapResolvedType(Series<N> series, int rank) {
        return switch (rank) {
            case RANK_BIG_DECIMAL, RANK_BIG_INTEGER -> new ResolvedNominalSeries<>(typeForRank(rank), series, hasNulls(series));
            case RANK_DOUBLE, RANK_FLOAT, RANK_LONG, RANK_INT -> series.getNominalType() == typeForRank(rank)
                    ? series
                    : new ResolvedNominalSeries<>(typeForRank(rank), series, hasNulls(series));
            default -> series;
        };
    }

    private static BigInteger toBigInteger(Number number) {
        if (number instanceof BigInteger bi) {
            return bi;
        }

        if (number instanceof BigDecimal bd) {
            return bd.toBigInteger();
        }

        return BigInteger.valueOf(number.longValue());
    }

    private static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal bd) {
            return bd;
        }

        if (number instanceof BigInteger bi) {
            return new BigDecimal(bi);
        }

        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte) {
            return BigDecimal.valueOf(number.longValue());
        }

        return BigDecimal.valueOf(number.doubleValue()).stripTrailingZeros();
    }

    private static ScanResult scanSeries(Series<?> series) {
        int rank = NO_RANK;
        boolean hasNulls = false;

        for (int i = 0; i < series.size(); i++) {
            Object value = series.get(i);
            if (value == null) {
                hasNulls = true;
            } else if (value instanceof Number n) {
                int vr = valueRank(n);
                if (vr < rank) {
                    rank = vr;
                    if (rank == RANK_BIG_DECIMAL) {
                        break;
                    }
                }
            } else {
                // Convert non-number to BigDecimal
                rank = RANK_BIG_DECIMAL;
                break;
            }
        }

        return new ScanResult(rank == NO_RANK ? RANK_BIG_DECIMAL : rank, hasNulls);
    }

    record ScanResult(int rank, boolean hasNulls) {
    }
}
