package org.dflib.parquet.read;

import dev.hardwood.metadata.ConvertedType;
import dev.hardwood.metadata.LogicalType;
import dev.hardwood.metadata.RepetitionType;
import dev.hardwood.row.PqInterval;
import dev.hardwood.row.StructAccessor;
import dev.hardwood.schema.SchemaNode;
import org.dflib.Series;

/**
 * Accumulates the values of a single Parquet column into a DFLib {@link Series}.
 *
 * @since 2.0.0
 */
public interface ColumnBuilder {

    void append(StructAccessor row, int fieldIndex);

    Series<?> toSeries();

    /**
     * Creates a builder producing the DFLib Series type that corresponds to the given Parquet column schema. Columns
     * that can't hold nulls (i.e. "required" ones) and have a primitive counterpart in DFLib are accumulated without
     * boxing. Everything else is accumulated as objects, and - when the writer dictionary-encoded the column, and the
     * values are neither mutable nor unsuitable as hash keys - compacted, so that repeated values share a single
     * instance.
     *
     * @param dictionaryEncoded whether the file carries a dictionary page for this column, i.e. whether the writer
     *                          found its values worth deduplicating
     */
    static ColumnBuilder of(SchemaNode colSchema, ConvertedType legacyType, boolean dictionaryEncoded, int capacity) {

        boolean allowsNulls = colSchema.repetitionType() != RepetitionType.REQUIRED;

        return switch (colSchema) {
            case SchemaNode.PrimitiveNode p -> primitive(p, legacyType, dictionaryEncoded, capacity, allowsNulls);
            case SchemaNode.GroupNode g -> group(g, capacity);
        };
    }

    private static ColumnBuilder primitive(
            SchemaNode.PrimitiveNode colSchema,
            ConvertedType legacyType,
            boolean dictionaryEncoded,
            int capacity,
            boolean allowsNulls) {

        LogicalType lt = LogicalTypes.effective(colSchema.logicalType(), legacyType);

        if (lt != null) {

            // STRING, ENUM and JSON all decode to a String. Hardwood already shares a single String instance per
            // dictionary entry, so these need no extra compaction
            if (lt instanceof LogicalType.StringType
                    || lt instanceof LogicalType.EnumType
                    || lt instanceof LogicalType.JsonType) {
                return ObjectColumnBuilder.of(capacity, false, StructAccessor::getString);
            }

            if (lt instanceof LogicalType.BsonType) {
                return ObjectColumnBuilder.of(capacity, false, StructAccessor::getBinary);
            }

            if (lt instanceof LogicalType.IntType it) {
                return it.isSigned()
                        ? signedInt(colSchema, it.bitWidth(), dictionaryEncoded, capacity, allowsNulls)
                        : unsignedInt(colSchema, it.bitWidth(), dictionaryEncoded, capacity, allowsNulls);
            }

            if (lt instanceof LogicalType.UuidType) {
                return ObjectColumnBuilder.of(capacity, dictionaryEncoded, StructAccessor::getUuid);
            }

            if (lt instanceof LogicalType.DateType) {
                return ObjectColumnBuilder.of(capacity, dictionaryEncoded, StructAccessor::getDate);
            }

            if (lt instanceof LogicalType.TimeType) {
                return ObjectColumnBuilder.of(capacity, dictionaryEncoded, StructAccessor::getTime);
            }

            if (lt instanceof LogicalType.TimestampType ts) {
                return ts.isAdjustedToUTC()
                        ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, StructAccessor::getTimestamp)
                        : ObjectColumnBuilder.of(capacity, dictionaryEncoded, StructAccessor::getLocalTimestamp);
            }

            if (lt instanceof LogicalType.DecimalType) {
                return ObjectColumnBuilder.of(capacity, dictionaryEncoded, StructAccessor::getDecimal);
            }

            if (lt instanceof LogicalType.Float16Type) {
                // Hardwood has no typed FLOAT16 accessor, but its generic "getValue" decodes FLOAT16 to a Float
                return allowsNulls
                        ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, (row, i) -> (Float) row.getValue(i))
                        : PrimitiveColumnBuilders.ofFloat(capacity, (row, i) -> (Float) row.getValue(i));
            }

            if (lt instanceof LogicalType.IntervalType) {
                // DFLib represents an interval as a 3-slot int[] of (months, days, millis)
                return ObjectColumnBuilder.of(capacity, false, (row, i) -> {
                    PqInterval v = row.getInterval(i);
                    return v != null ? new int[]{(int) v.months(), (int) v.days(), (int) v.milliseconds()} : null;
                });
            }

            if (lt instanceof LogicalType.NullType) {
                return ObjectColumnBuilder.of(capacity, false, (row, i) -> null);
            }

            // GEOMETRY and GEOGRAPHY carry opaque binary payloads, and anything unrecognized falls through to the
            // underlying physical type
        }

        return switch (colSchema.type()) {
            case BOOLEAN -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, false, nullable(StructAccessor::getBoolean))
                    : PrimitiveColumnBuilders.ofBool(capacity);
            case INT32 -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, nullable(StructAccessor::getInt))
                    : PrimitiveColumnBuilders.ofInt(capacity, StructAccessor::getInt);
            case INT64 -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, nullable(StructAccessor::getLong))
                    : PrimitiveColumnBuilders.ofLong(capacity, StructAccessor::getLong);
            case FLOAT -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, nullable(StructAccessor::getFloat))
                    : PrimitiveColumnBuilders.ofFloat(capacity, StructAccessor::getFloat);
            case DOUBLE -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, nullable(StructAccessor::getDouble))
                    : PrimitiveColumnBuilders.ofDouble(capacity);
            case BYTE_ARRAY, FIXED_LEN_BYTE_ARRAY -> ObjectColumnBuilder.of(capacity, false, StructAccessor::getBinary);
            case INT96 -> throw new IllegalArgumentException(
                    "INT96 deserialization is deprecated and is not supported: " + colSchema.name());
        };
    }

    private static ColumnBuilder signedInt(
            SchemaNode.PrimitiveNode colSchema,
            int bitWidth,
            boolean dictionaryEncoded,
            int capacity,
            boolean allowsNulls) {

        return switch (bitWidth) {

            // DFLib has no primitive byte and short Series, so these are boxed regardless of nullability
            case 8 -> ObjectColumnBuilder.of(capacity, dictionaryEncoded, (row, i) ->
                    row.isNull(i) ? null : (byte) row.getInt(i));
            case 16 -> ObjectColumnBuilder.of(capacity, dictionaryEncoded, (row, i) ->
                    row.isNull(i) ? null : (short) row.getInt(i));

            case 32 -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, nullable(StructAccessor::getInt))
                    : PrimitiveColumnBuilders.ofInt(capacity, StructAccessor::getInt);
            case 64 -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, nullable(StructAccessor::getLong))
                    : PrimitiveColumnBuilders.ofLong(capacity, StructAccessor::getLong);

            default -> throw new IllegalArgumentException(
                    "Invalid bit width for an int type: " + colSchema.name() + ": " + bitWidth);
        };
    }

    /**
     * Unsigned ints do not fit in the Java primitive of the same width, so each is read as the next larger type.
     * 64-bit unsigned ints have no primitive counterpart at all, and are read as BigInteger. Hardwood does no such
     * widening of its own - it passes raw two's complement values through - so DFLib widens them here.
     */
    private static ColumnBuilder unsignedInt(
            SchemaNode.PrimitiveNode colSchema,
            int bitWidth,
            boolean dictionaryEncoded,
            int capacity,
            boolean allowsNulls) {

        return switch (bitWidth) {

            case 8 -> ObjectColumnBuilder.of(capacity, dictionaryEncoded, (row, i) ->
                    row.isNull(i) ? null : (short) (row.getInt(i) & 0xFF));

            case 16 -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, (row, i) ->
                    row.isNull(i) ? null : row.getInt(i) & 0xFFFF)
                    : PrimitiveColumnBuilders.ofInt(capacity, (row, i) -> row.getInt(i) & 0xFFFF);

            case 32 -> allowsNulls
                    ? ObjectColumnBuilder.of(capacity, dictionaryEncoded, (row, i) ->
                    row.isNull(i) ? null : Integer.toUnsignedLong(row.getInt(i)))
                    : PrimitiveColumnBuilders.ofLong(capacity, (row, i) -> Integer.toUnsignedLong(row.getInt(i)));

            case 64 -> ObjectColumnBuilder.of(capacity, dictionaryEncoded, (row, i) ->
                    row.isNull(i) ? null : LogicalTypes.toUnsignedBigInteger(row.getLong(i)));

            default -> throw new IllegalArgumentException(
                    "Invalid bit width for an int type: " + colSchema.name() + ": " + bitWidth);
        };
    }

    private static ColumnBuilder group(SchemaNode.GroupNode colSchema, int capacity) {

        if (colSchema.isList()) {
            SchemaNode element = colSchema.getListElement();
            if (element == null) {
                throw new IllegalArgumentException("Unrecognized LIST layout: " + colSchema.name());
            }

            ElementReader elementReader = ElementReader.of(element);
            return ObjectColumnBuilder.of(capacity, false, (row, i) ->
                    row.isNull(i) ? null : elementReader.toList(row.getList(i)));
        }

        if (colSchema.isMap()) {
            SchemaNode key = colSchema.getMapKey();
            SchemaNode value = colSchema.getMapValue();
            if (key == null || value == null) {
                throw new IllegalArgumentException("Unrecognized MAP layout: " + colSchema.name());
            }

            ElementReader keyReader = ElementReader.of(key);
            ElementReader valueReader = ElementReader.of(value);
            return ObjectColumnBuilder.of(capacity, false, (row, i) ->
                    row.isNull(i) ? null : ElementReader.toMap(row.getMap(i), keyReader, valueReader));
        }

        throw new IllegalArgumentException(
                "Deserialization of this group type is not supported: " + colSchema.name());
    }

    /**
     * Wraps an accessor that returns a primitive in a null check, so that a null in an "optional" column does not
     * come back as the primitive default.
     */
    private static <T> ValueReader<T> nullable(ValueReader<T> reader) {
        return (row, i) -> row.isNull(i) ? null : reader.read(row, i);
    }
}
