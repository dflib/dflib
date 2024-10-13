package org.dflib.parquet;

import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;
import org.dflib.Extractor;
import org.dflib.Index;

import static org.apache.parquet.schema.LogicalTypeAnnotation.*;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.*;

class ColConfigurator {

    int srcColPos;
    String srcColName;
    boolean compact;

    private ColConfigurator() {
        srcColPos = -1;
    }

    static ColConfigurator objectCol(int pos, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.compact = compact;
        return config;
    }

    static ColConfigurator objectCol(String name, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.compact = compact;
        return config;
    }

    int srcPos(Index header) {
        return srcColPos >= 0 ? srcColPos : header.position(srcColName);
    }

    Extractor<Object[], ?> extractor(int srcPos, GroupType schema) {
        Extractor<Object[], ?> e = sparseExtractor(srcPos, schema.getFields().get(srcPos));
        return compact ? e.compact() : e;
    }

    private static Extractor<Object[], ?> sparseExtractor(int pos, Type colSchema) {
        Extractor<Object[], ?> extractor = logicalExtractor(pos, colSchema);

        if (extractor != null) {
            return extractor;
        }

        if (colSchema.isPrimitive()) {
            return primitiveExtractor(pos, colSchema);
        }

        throw new RuntimeException(colSchema.asGroupType().getName() + " deserialization not supported");
    }

    private static Extractor<Object[], ?> primitiveExtractor(int pos, Type colSchema) {
        PrimitiveType.PrimitiveTypeName type = colSchema.asPrimitiveType().getPrimitiveTypeName();
        if (colSchema.isRepetition(Type.Repetition.OPTIONAL)) {
            return Extractor.$col(r -> r[pos]);
        }
        switch (type) {
            case INT32:
                return Extractor.$int(r -> (Integer) r[pos]);
            case INT64:
                return Extractor.$long(r -> (Long) r[pos]);
            case FLOAT:
                return Extractor.$float(r -> (Float) r[pos]);
            case DOUBLE:
                return Extractor.$double(r -> (Double) r[pos]);
            case BOOLEAN:
                return Extractor.$bool(r -> (Boolean) r[pos]);
            default:
                throw new RuntimeException(type + " deserialization not supported");
        }
    }

    private static Extractor<Object[], ?> logicalExtractor(int pos, Type colSchema) {
        var logicalTypeAnnotation = colSchema.getLogicalTypeAnnotation();
        if (logicalTypeAnnotation == null) {
            return null;
        }
        Extractor<Object[], ?> defaultExtractor = Extractor.$col(r -> r[pos]);
        if (logicalTypeAnnotation.equals(stringType())) {
            return defaultExtractor;
        }
        if (logicalTypeAnnotation.equals(enumType())) {
            return defaultExtractor;
        }
        if (logicalTypeAnnotation instanceof LogicalTypeAnnotation.IntLogicalTypeAnnotation) {
            LogicalTypeAnnotation.IntLogicalTypeAnnotation intType = (LogicalTypeAnnotation.IntLogicalTypeAnnotation) logicalTypeAnnotation;
            if (intType.getBitWidth() == 8) {
                return defaultExtractor;
            }
            if (intType.getBitWidth() == 16) {
                return defaultExtractor;
            }
        }
        var primitiveTypeName = colSchema.asPrimitiveType().getPrimitiveTypeName();
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
