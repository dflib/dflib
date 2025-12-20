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
        LogicalTypeAnnotation logicalType = type.getLogicalTypeAnnotation();

        if (logicalType != null) {

            if (logicalType.equals(LogicalTypeAnnotation.stringType())) {
                return new StringConverter(consumer);
            }

            if (logicalType.equals(LogicalTypeAnnotation.enumType())) {
                return new StringConverter(consumer);
            }

            if (logicalType instanceof LogicalTypeAnnotation.IntLogicalTypeAnnotation intType) {
                return switch (intType.getBitWidth()) {
                    case 8 -> new ToByteConverter(consumer);
                    case 16 -> new ToShortConverter(consumer);
                    default -> new ToPrimitiveTypeConverter(consumer);
                };
            }

            if (logicalType.equals(LogicalTypeAnnotation.uuidType()) && name == FIXED_LEN_BYTE_ARRAY) {
                return new UuidConverter(consumer);
            }

            if (logicalType.equals(LogicalTypeAnnotation.dateType()) && name == INT32) {
                return new LocalDateConverter(consumer);
            }

            if (logicalType instanceof LogicalTypeAnnotation.TimeLogicalTypeAnnotation time && (name == INT32 || name == INT64)) {
                return new LocalTimeConverter(consumer, time.getUnit());
            }

            if (logicalType instanceof LogicalTypeAnnotation.TimestampLogicalTypeAnnotation ts && name == INT64) {
                return ts.isAdjustedToUTC()
                        ? new InstantConverter(consumer, ts.getUnit())
                        : new LocalDateTimeConverter(consumer, ts.getUnit());
            }

            if (logicalType instanceof LogicalTypeAnnotation.DecimalLogicalTypeAnnotation decimalType) {
                return new DecimalConverter(consumer, name, decimalType.getScale());
            }

            // TODO: should we throw on an unknown logical type instead of falling back to a bae primitive?
        }

        return switch (name) {

            // TODO: ToPrimitiveTypeConverter does boxing/unboxing. Should push primitive values to extractor directly
            case INT32, INT64, FLOAT, DOUBLE, BOOLEAN, BINARY, FIXED_LEN_BYTE_ARRAY ->
                    new ToPrimitiveTypeConverter(consumer);
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
