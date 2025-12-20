package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;

import java.util.function.Consumer;

import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.*;

class Converters {

    public static Converter converter(Type type, Consumer<Object> consumer) {

        if (type.isPrimitive()) {
            return primitiveConverter(type.asPrimitiveType(), consumer);
        } else if (type instanceof GroupType gt) {
            return groupConverter(gt, consumer);
        }

        throw new RuntimeException(type + " deserialization is not supported");
    }

    private static Converter primitiveConverter(PrimitiveType type, Consumer<Object> consumer) {

        PrimitiveType.PrimitiveTypeName name = type.getPrimitiveTypeName();
        LogicalTypeAnnotation lt = type.getLogicalTypeAnnotation();

        if (lt != null) {

            if (lt.equals(LogicalTypeAnnotation.stringType())) {
                return new StringConverter(consumer);
            }

            if (lt.equals(LogicalTypeAnnotation.enumType())) {
                return new StringConverter(consumer);
            }

            if (lt instanceof LogicalTypeAnnotation.IntLogicalTypeAnnotation intType) {
                return switch (intType.getBitWidth()) {
                    case 8 -> new ByteConverter(consumer);
                    case 16 -> new ShortConverter(consumer);
                    default -> new PrimitiveTypeConverter(consumer);
                };
            }

            if (lt.equals(LogicalTypeAnnotation.uuidType())) {
                if (name != FIXED_LEN_BYTE_ARRAY) {
                    throw new IllegalArgumentException("Can't decode as UUID: " + type);
                }

                return new UuidConverter(consumer);
            }

            if (lt.equals(LogicalTypeAnnotation.dateType())) {
                if (name != INT32) {
                    throw new IllegalArgumentException("Can't decode as DATE: " + type);
                }

                return new LocalDateConverter(consumer);
            }

            if (lt instanceof LogicalTypeAnnotation.TimeLogicalTypeAnnotation time) {
                return switch (name) {
                    case INT32 -> new LocalTimeMillisConverter(consumer);
                    case INT64 -> new LocalTimeMicrosNanosConverter(consumer, time.getUnit());
                    default -> throw new IllegalArgumentException("Can't decode as TIME: " + type);
                };
            }

            if (lt instanceof LogicalTypeAnnotation.TimestampLogicalTypeAnnotation ts) {
                if (name != INT64) {
                    throw new IllegalArgumentException("Can't decode as TIMESTAMP: " + type);
                }

                return ts.isAdjustedToUTC()
                        ? new InstantConverter(consumer, ts.getUnit())
                        : new LocalDateTimeConverter(consumer, ts.getUnit());
            }

            if (lt instanceof LogicalTypeAnnotation.DecimalLogicalTypeAnnotation dt) {
                return new DecimalConverter(consumer, name, dt.getScale());
            }

            if (lt instanceof LogicalTypeAnnotation.Float16LogicalTypeAnnotation) {
                return new Float16Converter(consumer);
            }

            // for unknown annotations fall through to the base primitive type
        }

        return switch (name) {

            // TODO: ToPrimitiveTypeConverter does boxing/unboxing. Should push primitive values to extractor directly
            case INT32, INT64, FLOAT, DOUBLE, BOOLEAN, BINARY, FIXED_LEN_BYTE_ARRAY ->
                    new PrimitiveTypeConverter(consumer);
            case INT96 -> throw new RuntimeException("INT96 deserialization is deprecated and is not supported");
        };
    }

    private static Converter groupConverter(GroupType type, Consumer<Object> consumer) {
        LogicalTypeAnnotation logicalType = type.getLogicalTypeAnnotation();

        if (logicalType instanceof LogicalTypeAnnotation.ListLogicalTypeAnnotation) {

            if (type.getType(0) instanceof GroupType et && type.getType(0).isRepetition(Type.Repetition.REPEATED)) {
                return new ToListConverter(et.getType(0), consumer);
            } else {
                throw new RuntimeException("Expected a 'repeated group' type within the LIST group. Got: " + type.getType(0));
            }
        } else if (logicalType instanceof LogicalTypeAnnotation.MapLogicalTypeAnnotation) {
            if (type.getType(0) instanceof GroupType et && type.getType(0).isRepetition(Type.Repetition.REPEATED)) {
                return new ToMapConverter(et.getType(0), et.getType(1), consumer);
            } else {
                throw new RuntimeException("Expected a 'repeated group' type within the MAP group. Got: " + type.getType(0));
            }
        }

        throw new RuntimeException(type + " deserialization is not supported");
    }
}
