package org.dflib.parquet;

import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;
import org.dflib.Extractor;
import org.dflib.Index;

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

        // if the main type has a logical type, it should be returned unchanged
        if (colSchema.getLogicalTypeAnnotation() != null) {
            return Extractor.$col(r -> r[pos]);
        }

        if (colSchema.isPrimitive()) {
            PrimitiveType.PrimitiveTypeName type = colSchema.asPrimitiveType().getPrimitiveTypeName();
            if (colSchema.isRepetition(Type.Repetition.OPTIONAL)) {
                return Extractor.$col(r -> r[pos]);
            }
            return switch (type) {
                case INT32 -> Extractor.$int(r -> (Integer) r[pos]);
                case INT64 -> Extractor.$long(r -> (Long) r[pos]);
                case FLOAT -> Extractor.$float(r -> (Float) r[pos]);
                case DOUBLE -> Extractor.$double(r -> (Double) r[pos]);
                case BOOLEAN -> Extractor.$bool(r -> (Boolean) r[pos]);
                case BINARY, FIXED_LEN_BYTE_ARRAY -> Extractor.$col(r -> r[pos]);
                case INT96 -> throw new RuntimeException("INT96 deserialization is deprecated and is not supported");
            };
        }

        return Extractor.$col(r -> r[pos]);
    }
}
