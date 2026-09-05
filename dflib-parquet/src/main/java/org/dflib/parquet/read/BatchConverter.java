package org.dflib.parquet.read;

import dev.hardwood.Validity;
import dev.hardwood.metadata.LogicalType;
import dev.hardwood.metadata.PhysicalType;
import dev.hardwood.reader.ColumnReader;
import dev.hardwood.schema.SchemaNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Converts one batch of a Parquet column into the objects DFLib stores for it.
 *
 * <p>Hardwood's batch API hands back the column's physical values - {@code int[]}, {@code long[]}, raw bytes - and
 * leaves the logical type unapplied, so the decoding of dates, decimals, uuids and the rest happens here, a batch at
 * a time rather than a value at a time.
 *
 * @since 2.0.0
 */
@FunctionalInterface
public interface BatchConverter {

    /**
     * Converts the reader's current batch, writing {@code count} values into {@code target} starting at
     * {@code offset}.
     */
    void convert(ColumnReader reader, Object[] target, int offset, int count);

    static BatchConverter of(SchemaNode.PrimitiveNode colSchema, LogicalType logicalType) {

        PhysicalType type = colSchema.type();

        if (logicalType != null) {

            if (logicalType instanceof LogicalType.StringType
                    || logicalType instanceof LogicalType.EnumType
                    || logicalType instanceof LogicalType.JsonType) {
                // Hardwood shares a String instance per dictionary entry, so these arrive compacted
                return (r, t, o, n) -> System.arraycopy(r.getStrings(), 0, t, o, n);
            }

            if (logicalType instanceof LogicalType.BsonType) {
                return (r, t, o, n) -> System.arraycopy(r.getBinaries(), 0, t, o, n);
            }

            if (logicalType instanceof LogicalType.IntType it) {
                return it.isSigned() ? signedInt(colSchema, it) : unsignedInt(colSchema, it);
            }

            if (logicalType instanceof LogicalType.UuidType) {
                return bytes((b, i) -> {
                    ByteBuffer bb = ByteBuffer.wrap(b);
                    return new UUID(bb.getLong(), bb.getLong());
                });
            }

            if (logicalType instanceof LogicalType.DateType) {
                return ints((v, i) -> LocalDate.ofEpochDay(v[i]));
            }

            if (logicalType instanceof LogicalType.TimeType time) {
                long perUnit = nanosPerUnit(time.unit());
                return type == PhysicalType.INT32
                        ? ints((v, i) -> LocalTime.ofNanoOfDay(v[i] * perUnit))
                        : longs((v, i) -> LocalTime.ofNanoOfDay(v[i] * perUnit));
            }

            if (logicalType instanceof LogicalType.TimestampType ts) {
                long perUnit = nanosPerUnit(ts.unit());
                long perSecond = 1_000_000_000L / perUnit;
                return ts.isAdjustedToUTC()
                        ? longs((v, i) -> instant(v[i], perUnit, perSecond))
                        : longs((v, i) -> localDateTime(v[i], perUnit, perSecond));
            }

            if (logicalType instanceof LogicalType.DecimalType dt) {
                int scale = dt.scale();
                return switch (type) {
                    case INT32 -> ints((v, i) -> BigDecimal.valueOf(v[i], scale));
                    case INT64 -> longs((v, i) -> BigDecimal.valueOf(v[i], scale));
                    case BYTE_ARRAY, FIXED_LEN_BYTE_ARRAY ->
                            bytes((b, i) -> new BigDecimal(new BigInteger(b), scale));
                    default -> throw new IllegalArgumentException(
                            "Can't decode as DECIMAL: " + colSchema.name());
                };
            }

            if (logicalType instanceof LogicalType.Float16Type) {
                return bytes((b, i) -> Float.float16ToFloat(
                        (short) ((b[0] & 0xFF) | ((b[1] & 0xFF) << 8))));
            }

            if (logicalType instanceof LogicalType.IntervalType) {
                // DFLib represents an interval as a 3-slot int[] of (months, days, millis)
                return bytes((b, i) -> {
                    ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
                    return new int[]{bb.getInt(0), bb.getInt(4), bb.getInt(8)};
                });
            }

            if (logicalType instanceof LogicalType.NullType) {
                return (r, t, o, n) -> {
                };
            }
        }

        return switch (type) {
            case BOOLEAN -> booleans();
            case INT32 -> ints((v, i) -> v[i]);
            case INT64 -> longs((v, i) -> v[i]);
            case FLOAT -> floats();
            case DOUBLE -> doubles();
            case BYTE_ARRAY, FIXED_LEN_BYTE_ARRAY ->
                    (r, t, o, n) -> System.arraycopy(r.getBinaries(), 0, t, o, n);
            case INT96 -> throw new IllegalArgumentException(
                    "INT96 deserialization is deprecated and is not supported: " + colSchema.name());
        };
    }

    private static BatchConverter signedInt(SchemaNode.PrimitiveNode colSchema, LogicalType.IntType it) {
        return switch (it.bitWidth()) {
            case 8 -> ints((v, i) -> (byte) v[i]);
            case 16 -> ints((v, i) -> (short) v[i]);
            case 32 -> ints((v, i) -> v[i]);
            case 64 -> longs((v, i) -> v[i]);
            default -> throw new IllegalArgumentException(
                    "Invalid bit width for an int type: " + colSchema.name() + ": " + it.bitWidth());
        };
    }

    private static BatchConverter unsignedInt(SchemaNode.PrimitiveNode colSchema, LogicalType.IntType it) {
        return switch (it.bitWidth()) {
            case 8 -> ints((v, i) -> (short) (v[i] & 0xFF));
            case 16 -> ints((v, i) -> v[i] & 0xFFFF);
            case 32 -> ints((v, i) -> Integer.toUnsignedLong(v[i]));
            case 64 -> longs((v, i) -> LogicalTypes.toUnsignedBigInteger(v[i]));
            default -> throw new IllegalArgumentException(
                    "Invalid bit width for an int type: " + colSchema.name() + ": " + it.bitWidth());
        };
    }

    @FunctionalInterface
    interface FromInts {
        Object get(int[] values, int i);
    }

    @FunctionalInterface
    interface FromLongs {
        Object get(long[] values, int i);
    }

    @FunctionalInterface
    interface FromBytes {
        Object get(byte[] value, int i);
    }

    private static BatchConverter ints(FromInts f) {
        return (r, t, o, n) -> {
            int[] values = r.getInts();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : f.get(values, i);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = f.get(values, i);
                }
            }
        };
    }

    private static BatchConverter longs(FromLongs f) {
        return (r, t, o, n) -> {
            long[] values = r.getLongs();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : f.get(values, i);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = f.get(values, i);
                }
            }
        };
    }

    private static BatchConverter bytes(FromBytes f) {
        return (r, t, o, n) -> {
            byte[][] values = r.getBinaries();
            for (int i = 0; i < n; i++) {
                byte[] v = values[i];
                t[o + i] = v == null ? null : f.get(v, i);
            }
        };
    }

    private static BatchConverter booleans() {
        return (r, t, o, n) -> {
            boolean[] values = r.getBooleans();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : values[i];
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = values[i];
                }
            }
        };
    }

    private static BatchConverter floats() {
        return (r, t, o, n) -> {
            float[] values = r.getFloats();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : values[i];
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = values[i];
                }
            }
        };
    }

    private static BatchConverter doubles() {
        return (r, t, o, n) -> {
            double[] values = r.getDoubles();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : values[i];
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = values[i];
                }
            }
        };
    }

    private static long nanosPerUnit(LogicalType.TimeUnit unit) {
        return switch (unit) {
            case MILLIS -> 1_000_000L;
            case MICROS -> 1_000L;
            case NANOS -> 1L;
        };
    }

    private static Instant instant(long value, long nanosPerUnit, long unitsPerSecond) {
        return Instant.ofEpochSecond(
                Math.floorDiv(value, unitsPerSecond),
                Math.floorMod(value, unitsPerSecond) * nanosPerUnit);
    }

    private static LocalDateTime localDateTime(long value, long nanosPerUnit, long unitsPerSecond) {
        return LocalDateTime.ofEpochSecond(
                Math.floorDiv(value, unitsPerSecond),
                (int) (Math.floorMod(value, unitsPerSecond) * nanosPerUnit),
                ZoneOffset.UTC);
    }
}
