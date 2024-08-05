package org.dflib.parquet.read;

import static org.apache.parquet.schema.LogicalTypeAnnotation.dateType;
import static org.apache.parquet.schema.LogicalTypeAnnotation.enumType;
import static org.apache.parquet.schema.LogicalTypeAnnotation.stringType;
import static org.apache.parquet.schema.LogicalTypeAnnotation.uuidType;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.INT32;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.INT64;

import org.apache.parquet.schema.LogicalTypeAnnotation.DecimalLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.IntLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimeLogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimestampLogicalTypeAnnotation;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.Type.Repetition;
import org.dflib.Extractor;

public class RowExtractorFactory {

    public static Extractor<Row, ?> converterFor(Type schemaField, int idx) {
        Extractor<Row, ?> converter = buildFromLogicalTypeConverter(schemaField, idx);
        if (converter != null) {
            return converter;
        }
        if (schemaField.isPrimitive()) {
            return buildPrimitiveExtractor(schemaField, idx);
        }
        throw new RuntimeException(schemaField.asGroupType().getName() + " deserialization not supported");
    }

    private static Extractor<Row, ?> buildPrimitiveExtractor(Type parquetField, int idx) {
        PrimitiveTypeName type = parquetField.asPrimitiveType().getPrimitiveTypeName();
        if (parquetField.isRepetition(Repetition.OPTIONAL)) {
            return Extractor.$col(r -> r.get(idx));
        }
        switch (type) {
        case INT32:
            return Extractor.$int(r -> (Integer) r.get(idx));
        case INT64:
            return Extractor.$long(r -> (Long) r.get(idx));
        case FLOAT:
            return Extractor.$col(r -> r.get(idx));
        case DOUBLE:
            return Extractor.$double(r -> (Double) r.get(idx));
        case BOOLEAN:
            return Extractor.$bool(r -> (Boolean) r.get(idx));
        default:
            throw new RuntimeException(type + " deserialization not supported");
        }
    }

    private static Extractor<Row, ?> buildFromLogicalTypeConverter(Type parquetField, int idx) {
        var logicalTypeAnnotation = parquetField.getLogicalTypeAnnotation();
        if (logicalTypeAnnotation == null) {
            return null;
        }
        Extractor<Row, ?> defaultExtractor = Extractor.$col(r -> r.get(idx));
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
