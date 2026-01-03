package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;
import org.dflib.builder.ValueAccum;
import org.dflib.builder.ValueHolder;
import org.dflib.builder.ValueStore;

import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.*;

/**
 * Combines a Parquet {@link Converter} with a DFLib {@link ValueAccum} for a given column schema.
 *
 * @since 2.0.0
 */
public interface StoringConverter {

    int DEFAULT_CAPACITY = 1000;

    // Since Parquet base Converter is a class, we can't make ValueStoreConverter both a Converter and a Store.
    // So implementing converters will simply return "this" here.
    Converter converter();

    boolean allowsNulls();

    ValueStore<?> store();

    default ValueAccum<?> accum() {
        return (ValueAccum<?>) store();
    }

    default ValueHolder<?> holder() {
        return (ValueHolder<?>) store();
    }

    static StoringConverter of(Type colSchema, boolean accum, boolean dictionarySupport) {

        boolean allowsNulls = colSchema.getRepetition() == Type.Repetition.OPTIONAL;

        if (colSchema.isPrimitive()) {
            return primitiveConverter(colSchema.asPrimitiveType(), accum, dictionarySupport, allowsNulls);
        } else if (colSchema instanceof GroupType gt) {
            return groupConverter(gt, accum, allowsNulls);
        }

        throw new RuntimeException(colSchema + " deserialization is not supported");
    }

    private static StoringConverter primitiveConverter(
            PrimitiveType colSchema,
            boolean accum,
            boolean dictionarySupport,
            boolean allowsNulls) {

        PrimitiveType.PrimitiveTypeName name = colSchema.getPrimitiveTypeName();
        LogicalTypeAnnotation lt = colSchema.getLogicalTypeAnnotation();

        if (lt != null) {

            if (lt.equals(LogicalTypeAnnotation.stringType())) {
                return StringConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
            }

            if (lt.equals(LogicalTypeAnnotation.enumType())) {
                return StringConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
            }

            if (lt instanceof LogicalTypeAnnotation.IntLogicalTypeAnnotation intType) {
                return switch (intType.getBitWidth()) {
                    // TODO: we don't have primitive byte and short Series.. The data is passed as ints anyways,
                    //  so should we produce IntSeries for those? ByteSeries actually sounds like a good addition to DFLib
                    case 8 -> ByteConverter.of(accum, DEFAULT_CAPACITY, allowsNulls);
                    case 16 -> ShortConverter.of(accum, DEFAULT_CAPACITY, allowsNulls);

                    case 32 -> IntConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
                    case 64 -> LongConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);

                    default -> throw new IllegalArgumentException("Invalid bit width for an int type: " + colSchema);
                };
            }

            if (lt.equals(LogicalTypeAnnotation.uuidType())) {
                if (name != FIXED_LEN_BYTE_ARRAY) {
                    throw new IllegalArgumentException("Can't decode as UUID, must be a 'FIXED_LEN_BYTE_ARRAY': " + colSchema);
                }

                return UuidConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
            }

            if (lt.equals(LogicalTypeAnnotation.dateType())) {
                if (name != INT32) {
                    throw new IllegalArgumentException("Can't decode as DATE: " + colSchema);
                }

                return LocalDateConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
            }

            if (lt instanceof LogicalTypeAnnotation.TimeLogicalTypeAnnotation time) {
                return switch (name) {
                    case INT32 -> LocalTimeMillisConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
                    case INT64 ->
                            LocalTimeMicrosNanosConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls, time.getUnit());
                    default -> throw new IllegalArgumentException("Can't decode as TIME: " + colSchema);
                };
            }

            if (lt instanceof LogicalTypeAnnotation.TimestampLogicalTypeAnnotation ts) {
                if (name != INT64) {
                    throw new IllegalArgumentException("Can't decode as TIMESTAMP: " + colSchema);
                }

                return ts.isAdjustedToUTC()
                        ? InstantConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls, ts.getUnit())
                        : LocalDateTimeConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls, ts.getUnit());
            }

            if (lt instanceof LogicalTypeAnnotation.DecimalLogicalTypeAnnotation dt) {
                return DecimalConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls, name, dt.getScale());
            }

            if (lt instanceof LogicalTypeAnnotation.Float16LogicalTypeAnnotation) {
                return Float16Converter.of(accum, DEFAULT_CAPACITY, allowsNulls);
            }

            if (lt instanceof LogicalTypeAnnotation.IntervalLogicalTypeAnnotation) {
                return IntervalConverter.of(accum, DEFAULT_CAPACITY, allowsNulls);
            }

            // for unknown annotations fall through to the base primitive type
        }

        return switch (name) {
            case INT32 -> IntConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
            case INT64 -> LongConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
            case FLOAT -> FloatConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
            case DOUBLE -> DoubleConverter.of(accum, DEFAULT_CAPACITY, dictionarySupport, allowsNulls);
            case BOOLEAN -> BoolConverter.of(accum, DEFAULT_CAPACITY, allowsNulls);
            case BINARY, FIXED_LEN_BYTE_ARRAY -> BytesConverter.of(accum, DEFAULT_CAPACITY, allowsNulls);
            case INT96 -> throw new RuntimeException("INT96 deserialization is deprecated and is not supported");
        };
    }

    private static StoringConverter groupConverter(GroupType colSchema, boolean accum, boolean allowsNulls) {
        LogicalTypeAnnotation logicalType = colSchema.getLogicalTypeAnnotation();

        if (logicalType instanceof LogicalTypeAnnotation.ListLogicalTypeAnnotation) {

            if (colSchema.getType(0) instanceof GroupType et && colSchema.getType(0).isRepetition(Type.Repetition.REPEATED)) {
                return ToListConverter.of(accum, DEFAULT_CAPACITY, allowsNulls, et.getType(0));
            } else {
                throw new RuntimeException("Expected a 'repeated group' type within the LIST group. Got: " + colSchema.getType(0));
            }
        } else if (logicalType instanceof LogicalTypeAnnotation.MapLogicalTypeAnnotation) {
            if (colSchema.getType(0) instanceof GroupType et && colSchema.getType(0).isRepetition(Type.Repetition.REPEATED)) {
                return ToMapConverter.of(accum, DEFAULT_CAPACITY, allowsNulls, et.getType(0), et.getType(1));
            } else {
                throw new RuntimeException("Expected a 'repeated group' type within the MAP group. Got: " + colSchema.getType(0));
            }
        }

        throw new RuntimeException(colSchema + " deserialization is not supported");
    }
}
