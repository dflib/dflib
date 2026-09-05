package org.dflib.hardwood.write;

import dev.hardwood.writer.ColumnBatch;
import org.dflib.BooleanSeries;
import org.dflib.DoubleSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.hardwood.TimeUnit;

import java.math.BigDecimal;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Writes a range of one DataFrame column into a Parquet column batch.
 *
 * <p>The counterpart of {@link RowFieldWriter}, which writes the same data a row at a time. Hardwood's batch API
 * takes whole arrays of physical values, which is the shape DFLib already stores its columns in, so a primitive
 * column reaches the writer without being visited value by value.
 *
 * @since 2.0.0
 */
@FunctionalInterface
public interface BatchFieldWriter {

    /**
     * Adds {@code len} values of this column, starting at row {@code start}, to the batch.
     */
    void write(ColumnBatch batch, int start, int len);

    static BatchFieldWriter of(ColumnMeta column, Series<?> series, WriteConfiguration config) {

        String name = column.getColumnName();

        if (column.isEnum()) {
            return binary(name, series, v -> utf8(((Enum<?>) v).name()));
        }

        return switch (column.getInferredTypeName()) {

            case "int" -> {
                int[] values = ((IntSeries) series).toIntArray();
                yield (b, s, n) -> b.ints(name, slice(values, s, n));
            }
            case "long" -> {
                long[] values = ((LongSeries) series).toLongArray();
                yield (b, s, n) -> b.longs(name, slice(values, s, n));
            }
            case "double" -> {
                double[] values = ((DoubleSeries) series).toDoubleArray();
                yield (b, s, n) -> b.doubles(name, slice(values, s, n));
            }
            case "boolean" -> {
                boolean[] values = ((BooleanSeries) series).toBoolArray();
                yield (b, s, n) -> b.booleans(name, slice(values, s, n));
            }

            case "java.lang.Integer" -> ints(name, series, v -> (Integer) v);
            case "java.lang.Byte" -> ints(name, series, v -> (int) (Byte) v);
            case "java.lang.Short" -> ints(name, series, v -> (int) (Short) v);
            case "java.lang.Long" -> longs(name, series, v -> (Long) v);
            case "java.lang.Double" -> doubles(name, series);
            case "java.lang.Float" -> floats(name, series);
            case "java.lang.Boolean" -> booleans(name, series);

            case "java.lang.String" -> binary(name, series, v -> utf8((String) v));
            case "java.util.UUID" -> fixed(name, series, v -> uuidToBytes((UUID) v));

            case "java.time.LocalDate" -> ints(name, series, v -> Math.toIntExact(((LocalDate) v).toEpochDay()));
            case "java.time.LocalTime" -> {
                long perUnit = nanosPerUnit(config.timeUnit());
                yield config.timeUnit() == TimeUnit.MILLIS
                        ? ints(name, series, v -> Math.toIntExact(((LocalTime) v).toNanoOfDay() / perUnit))
                        : longs(name, series, v -> ((LocalTime) v).toNanoOfDay() / perUnit);
            }
            case "java.time.LocalDateTime" -> {
                long perUnit = nanosPerUnit(config.timeUnit());
                long perSecond = 1_000_000_000L / perUnit;
                yield longs(name, series, v -> {
                    LocalDateTime d = (LocalDateTime) v;
                    return d.toEpochSecond(ZoneOffset.UTC) * perSecond + d.getNano() / perUnit;
                });
            }
            case "java.time.Instant" -> {
                long perUnit = nanosPerUnit(config.timeUnit());
                long perSecond = 1_000_000_000L / perUnit;
                yield longs(name, series, v -> {
                    Instant i = (Instant) v;
                    return i.getEpochSecond() * perSecond + i.getNano() / perUnit;
                });
            }

            case "java.math.BigDecimal" -> decimal(name, series, config);

            default -> throw new IllegalArgumentException("Unsupported type " + column.getInferredTypeName());
        };
    }

    private static BatchFieldWriter decimal(String name, Series<?> series, WriteConfiguration config) {

        BigDecimalWriter decimals = new BigDecimalWriter(config.decimalConfig());
        int precision = config.decimalConfig().precision();

        if (precision <= 9) {
            return ints(name, series, v -> decimals.rescale((BigDecimal) v).unscaledValue().intValueExact());
        }
        if (precision <= 18) {
            return longs(name, series, v -> decimals.rescale((BigDecimal) v).unscaledValue().longValueExact());
        }

        return binary(name, series, v -> decimals.rescale((BigDecimal) v).unscaledValue().toByteArray());
    }

    @FunctionalInterface
    interface ToInt {
        int convert(Object value);
    }

    @FunctionalInterface
    interface ToLong {
        long convert(Object value);
    }

    @FunctionalInterface
    interface ToBytes {
        byte[] convert(Object value);
    }

    private static BatchFieldWriter ints(String name, Series<?> series, ToInt f) {
        return (b, s, n) -> {
            int[] values = new int[n];
            boolean[] nulls = new boolean[n];
            for (int i = 0; i < n; i++) {
                Object v = series.get(s + i);
                if (v == null) {
                    nulls[i] = true;
                } else {
                    values[i] = f.convert(v);
                }
            }
            b.ints(name, values, nulls);
        };
    }

    private static BatchFieldWriter longs(String name, Series<?> series, ToLong f) {
        return (b, s, n) -> {
            long[] values = new long[n];
            boolean[] nulls = new boolean[n];
            for (int i = 0; i < n; i++) {
                Object v = series.get(s + i);
                if (v == null) {
                    nulls[i] = true;
                } else {
                    values[i] = f.convert(v);
                }
            }
            b.longs(name, values, nulls);
        };
    }

    private static BatchFieldWriter doubles(String name, Series<?> series) {
        return (b, s, n) -> {
            double[] values = new double[n];
            boolean[] nulls = new boolean[n];
            for (int i = 0; i < n; i++) {
                Object v = series.get(s + i);
                if (v == null) {
                    nulls[i] = true;
                } else {
                    values[i] = (Double) v;
                }
            }
            b.doubles(name, values, nulls);
        };
    }

    private static BatchFieldWriter floats(String name, Series<?> series) {
        return (b, s, n) -> {
            float[] values = new float[n];
            boolean[] nulls = new boolean[n];
            for (int i = 0; i < n; i++) {
                Object v = series.get(s + i);
                if (v == null) {
                    nulls[i] = true;
                } else {
                    values[i] = (Float) v;
                }
            }
            b.floats(name, values, nulls);
        };
    }

    private static BatchFieldWriter booleans(String name, Series<?> series) {
        return (b, s, n) -> {
            boolean[] values = new boolean[n];
            boolean[] nulls = new boolean[n];
            for (int i = 0; i < n; i++) {
                Object v = series.get(s + i);
                if (v == null) {
                    nulls[i] = true;
                } else {
                    values[i] = (Boolean) v;
                }
            }
            b.booleans(name, values, nulls);
        };
    }

    private static BatchFieldWriter binary(String name, Series<?> series, ToBytes f) {
        return (b, s, n) -> b.bytes(name, bytes(series, s, n, f), nulls(series, s, n));
    }

    private static BatchFieldWriter fixed(String name, Series<?> series, ToBytes f) {
        return (b, s, n) -> b.fixed(name, bytes(series, s, n, f), nulls(series, s, n));
    }

    private static byte[][] bytes(Series<?> series, int start, int len, ToBytes f) {
        byte[][] values = new byte[len][];
        for (int i = 0; i < len; i++) {
            Object v = series.get(start + i);
            values[i] = v != null ? f.convert(v) : null;
        }
        return values;
    }

    private static boolean[] nulls(Series<?> series, int start, int len) {
        boolean[] nulls = new boolean[len];
        for (int i = 0; i < len; i++) {
            nulls[i] = series.get(start + i) == null;
        }
        return nulls;
    }

    private static byte[] utf8(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] uuidToBytes(UUID uuid) {
        byte[] out = new byte[16];
        ByteBuffer.wrap(out).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return out;
    }

    private static long nanosPerUnit(TimeUnit unit) {
        return switch (unit) {
            case MILLIS -> 1_000_000L;
            case MICROS -> 1_000L;
            case NANOS -> 1L;
        };
    }

    private static int[] slice(int[] values, int start, int len) {
        return start == 0 && len == values.length ? values : Arrays.copyOfRange(values, start, start + len);
    }

    private static long[] slice(long[] values, int start, int len) {
        return start == 0 && len == values.length ? values : Arrays.copyOfRange(values, start, start + len);
    }

    private static double[] slice(double[] values, int start, int len) {
        return start == 0 && len == values.length ? values : Arrays.copyOfRange(values, start, start + len);
    }

    private static boolean[] slice(boolean[] values, int start, int len) {
        return start == 0 && len == values.length ? values : Arrays.copyOfRange(values, start, start + len);
    }
}
