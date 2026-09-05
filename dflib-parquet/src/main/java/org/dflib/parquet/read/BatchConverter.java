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
 * <p>When the file dictionary-encoded the column, decoding goes through a {@link LongKeyDictionary} or a
 * {@link BytesKeyDictionary} keyed on the physical value, so each distinct value is decoded once and the result is
 * shared by every row repeating it. A converter is therefore stateful, and belongs to a single column read by a
 * single thread.
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

    /**
     * @param dictionary whether to decode through a dictionary of physical values, sharing one decoded instance
     *                   across the rows that repeat it. Worth doing when the file dictionary-encoded the column,
     *                   and sound only for values that are immutable and not handed to the caller by reference.
     */
    static BatchConverter of(SchemaNode.PrimitiveNode colSchema, LogicalType logicalType, boolean dictionary) {

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
                return it.isSigned()
                        ? signedInt(colSchema, it, dictionary)
                        : unsignedInt(colSchema, it, dictionary);
            }

            if (logicalType instanceof LogicalType.UuidType) {
                return bytes(dictionary, (b, off, len) -> {
                    ByteBuffer bb = ByteBuffer.wrap(b, off, len);
                    return new UUID(bb.getLong(), bb.getLong());
                });
            }

            if (logicalType instanceof LogicalType.DateType) {
                return ints(dictionary, v -> LocalDate.ofEpochDay(v));
            }

            if (logicalType instanceof LogicalType.TimeType time) {
                long perUnit = nanosPerUnit(time.unit());
                return type == PhysicalType.INT32
                        ? ints(dictionary, v -> LocalTime.ofNanoOfDay(v * perUnit))
                        : longs(dictionary, v -> LocalTime.ofNanoOfDay(v * perUnit));
            }

            if (logicalType instanceof LogicalType.TimestampType ts) {
                long perUnit = nanosPerUnit(ts.unit());
                long perSecond = 1_000_000_000L / perUnit;
                return ts.isAdjustedToUTC()
                        ? longs(dictionary, v -> instant(v, perUnit, perSecond))
                        : longs(dictionary, v -> localDateTime(v, perUnit, perSecond));
            }

            if (logicalType instanceof LogicalType.DecimalType dt) {
                int scale = dt.scale();
                return switch (type) {
                    case INT32 -> ints(dictionary, v -> BigDecimal.valueOf(v, scale));
                    case INT64 -> longs(dictionary, v -> BigDecimal.valueOf(v, scale));
                    case BYTE_ARRAY, FIXED_LEN_BYTE_ARRAY ->
                            bytes(dictionary, (b, off, len) -> new BigDecimal(new BigInteger(b, off, len), scale));
                    default -> throw new IllegalArgumentException(
                            "Can't decode as DECIMAL: " + colSchema.name());
                };
            }

            if (logicalType instanceof LogicalType.Float16Type) {
                return bytes(dictionary, (b, off, len) -> Float.float16ToFloat(
                        (short) ((b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8))));
            }

            if (logicalType instanceof LogicalType.IntervalType) {
                // DFLib represents an interval as a 3-slot int[] of (months, days, millis)
                return bytes(dictionary, (b, off, len) -> {
                    ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
                    return new int[]{bb.getInt(off), bb.getInt(off + 4), bb.getInt(off + 8)};
                });
            }

            if (logicalType instanceof LogicalType.NullType) {
                return (r, t, o, n) -> {
                };
            }
        }

        return switch (type) {
            case BOOLEAN -> booleans();
            case INT32 -> ints(dictionary, v -> v);
            case INT64 -> longs(dictionary, v -> v);
            case FLOAT -> floats(dictionary);
            case DOUBLE -> doubles(dictionary);
            case BYTE_ARRAY, FIXED_LEN_BYTE_ARRAY ->
                    (r, t, o, n) -> System.arraycopy(r.getBinaries(), 0, t, o, n);
            case INT96 -> throw new IllegalArgumentException(
                    "INT96 deserialization is deprecated and is not supported: " + colSchema.name());
        };
    }

    private static BatchConverter signedInt(
            SchemaNode.PrimitiveNode colSchema,
            LogicalType.IntType it,
            boolean dictionary) {

        return switch (it.bitWidth()) {
            case 8 -> ints(dictionary, v -> (byte) v);
            case 16 -> ints(dictionary, v -> (short) v);
            case 32 -> ints(dictionary, v -> v);
            case 64 -> longs(dictionary, v -> v);
            default -> throw new IllegalArgumentException(
                    "Invalid bit width for an int type: " + colSchema.name() + ": " + it.bitWidth());
        };
    }

    private static BatchConverter unsignedInt(
            SchemaNode.PrimitiveNode colSchema,
            LogicalType.IntType it,
            boolean dictionary) {

        return switch (it.bitWidth()) {
            case 8 -> ints(dictionary, v -> (short) (v & 0xFF));
            case 16 -> ints(dictionary, v -> v & 0xFFFF);
            case 32 -> ints(dictionary, v -> Integer.toUnsignedLong(v));
            case 64 -> longs(dictionary, v -> LogicalTypes.toUnsignedBigInteger(v));
            default -> throw new IllegalArgumentException(
                    "Invalid bit width for an int type: " + colSchema.name() + ": " + it.bitWidth());
        };
    }

    @FunctionalInterface
    interface FromInt {
        Object get(int value);
    }

    @FunctionalInterface
    interface FromLong {
        Object get(long value);
    }

    @FunctionalInterface
    interface FromFloat {
        Object get(float value);
    }

    @FunctionalInterface
    interface FromDouble {
        Object get(double value);
    }

    @FunctionalInterface
    interface FromBytes {
        Object get(byte[] buffer, int offset, int len);
    }

    private static BatchConverter ints(boolean dictionary, FromInt f) {
        FromInt decoder = dictionary ? intDictionary(f) : f;
        return (r, t, o, n) -> {
            int[] values = r.getInts();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : decoder.get(values[i]);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = decoder.get(values[i]);
                }
            }
        };
    }

    private static BatchConverter longs(boolean dictionary, FromLong f) {
        FromLong decoder = dictionary ? longDictionary(f) : f;
        return (r, t, o, n) -> {
            long[] values = r.getLongs();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : decoder.get(values[i]);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = decoder.get(values[i]);
                }
            }
        };
    }

    /**
     * Reads values out of the batch's shared byte buffer rather than through {@link ColumnReader#getBinaries()},
     * which would copy every value into a {@code byte[]} of its own on the way to being decoded and discarded.
     */
    private static BatchConverter bytes(boolean dictionary, FromBytes f) {
        FromBytes decoder = dictionary ? bytesDictionary(f) : f;
        return (r, t, o, n) -> {
            byte[] buffer = r.getBinaryValues();
            int[] offsets = r.getBinaryOffsets();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i)
                            ? null
                            : decoder.get(buffer, offsets[i], offsets[i + 1] - offsets[i]);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = decoder.get(buffer, offsets[i], offsets[i + 1] - offsets[i]);
                }
            }
        };
    }

    private static BatchConverter floats(boolean dictionary) {
        FromFloat decoder = dictionary ? floatDictionary(v -> v) : v -> v;
        return (r, t, o, n) -> {
            float[] values = r.getFloats();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : decoder.get(values[i]);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = decoder.get(values[i]);
                }
            }
        };
    }

    private static BatchConverter doubles(boolean dictionary) {
        FromDouble decoder = dictionary ? doubleDictionary(v -> v) : v -> v;
        return (r, t, o, n) -> {
            double[] values = r.getDoubles();
            Validity validity = r.getLeafValidity();
            if (validity.hasNulls()) {
                for (int i = 0; i < n; i++) {
                    t[o + i] = validity.isNull(i) ? null : decoder.get(values[i]);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    t[o + i] = decoder.get(values[i]);
                }
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

    // Every fixed-width key rides the same long-keyed dictionary: an int key is a long key that happens to fit, and
    // a float or double is keyed on its raw bits, so that -0.0 and the NaN payloads stay as distinct as the file
    // keeps them.

    private static FromInt intDictionary(FromInt f) {
        LongKeyDictionary dictionary = new LongKeyDictionary();
        return v -> {
            Object shared = dictionary.get(v);
            if (shared != null) {
                return shared;
            }
            Object decoded = f.get(v);
            dictionary.put(v, decoded);
            return decoded;
        };
    }

    private static FromLong longDictionary(FromLong f) {
        LongKeyDictionary dictionary = new LongKeyDictionary();
        return v -> {
            Object shared = dictionary.get(v);
            if (shared != null) {
                return shared;
            }
            Object decoded = f.get(v);
            dictionary.put(v, decoded);
            return decoded;
        };
    }

    private static FromFloat floatDictionary(FromFloat f) {
        LongKeyDictionary dictionary = new LongKeyDictionary();
        return v -> {
            long key = Float.floatToRawIntBits(v);
            Object shared = dictionary.get(key);
            if (shared != null) {
                return shared;
            }
            Object decoded = f.get(v);
            dictionary.put(key, decoded);
            return decoded;
        };
    }

    private static FromDouble doubleDictionary(FromDouble f) {
        LongKeyDictionary dictionary = new LongKeyDictionary();
        return v -> {
            long key = Double.doubleToRawLongBits(v);
            Object shared = dictionary.get(key);
            if (shared != null) {
                return shared;
            }
            Object decoded = f.get(v);
            dictionary.put(key, decoded);
            return decoded;
        };
    }

    private static FromBytes bytesDictionary(FromBytes f) {
        BytesKeyDictionary dictionary = new BytesKeyDictionary();
        return (b, off, len) -> {
            Object shared = dictionary.get(b, off, len);
            if (shared != null) {
                return shared;
            }
            Object decoded = f.get(b, off, len);
            dictionary.put(b, off, len, decoded);
            return decoded;
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
