package org.dflib.parquet.read;

import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.Type.Repetition;
import org.dflib.Extractor;

import static org.apache.parquet.schema.LogicalTypeAnnotation.*;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.*;

public class RowExtractorFactory {

    public static Extractor<Object[], ?> converterFor(Type schemaField, int idx) {
        Extractor<Object[], ?> converter = buildFromLogicalTypeConverter(schemaField, idx);
        if (converter != null) {
            return converter;
        }
        if (schemaField.isPrimitive()) {
            return buildPrimitiveExtractor(schemaField, idx);
        }
        throw new RuntimeException(schemaField.asGroupType().getName() + " deserialization not supported");
    }

    private static Extractor<Object[], ?> buildPrimitiveExtractor(Type parquetField, int idx) {
        PrimitiveTypeName type = parquetField.asPrimitiveType().getPrimitiveTypeName();
        if (parquetField.isRepetition(Repetition.OPTIONAL)) {
            return Extractor.$col(r -> r[idx]);
        }
        switch (type) {
        case INT32:
            return Extractor.$int(r -> (Integer) r[idx]);
        case INT64:
            return Extractor.$long(r -> (Long) r[idx]);
        case FLOAT:
            return Extractor.$col(r -> r[idx]);
        case DOUBLE:
            return Extractor.$double(r -> (Double) r[idx]);
        case BOOLEAN:
            return Extractor.$bool(r -> (Boolean) r[idx]);
        default:
            throw new RuntimeException(type + " deserialization not supported");
        }
    }

    private static Extractor<Object[], ?> buildFromLogicalTypeConverter(Type parquetField, int idx) {
        var logicalTypeAnnotation = parquetField.getLogicalTypeAnnotation();
        if (logicalTypeAnnotation == null) {
            return null;
        }
        Extractor<Object[], ?> defaultExtractor = Extractor.$col(r -> r[idx]);
        if (logicalTypeAnnotation.equals(stringType())) {
            return defaultExtractor;
        }
        if (logicalTypeAnnotation.equals(enumType())) {
            return defaultExtractor;
        }
        if (logicalTypeAnnotation instanceof IntLogicalTypeAnnotation) {
            IntLogicalTypeAnnotation intType = (IntLogicalTypeAnnotation) logicalTypeAnnotation;
            if (intType.getBitWidth() == 8) {
                return defaultExtractor;
            }
            if (intType.getBitWidth() == 16) {
                return defaultExtractor;
            }
        }
        var primitiveTypeName = parquetField.asPrimitiveType().getPrimitiveTypeName();
        if (logicalTypeAnnotation.equals(uuidType())
                && primitiveTypeName == FIXED_LEN_BYTE_ARRAY) {
            return defaultExtractor;
        }
        if (logicalTypeAnnotation.equals(dateType())
                && primitiveTypeName == INT32) {
            return defaultExtractor;
        }
        if (logicalTypeAnnotation instanceof TimeLogicalTypeAnnotation
                && (primitiveTypeName == INT32 || primitiveTypeName == INT64)) {
            return defaultExtractor;
        }
        if (logicalTypeAnnotation instanceof TimestampLogicalTypeAnnotation
                && primitiveTypeName == INT64) {
            return defaultExtractor;
        }
        if (logicalTypeAnnotation instanceof DecimalLogicalTypeAnnotation) {
            return defaultExtractor;
        }
        return null;
    }

}
