package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.ValueMapper;

import java.util.List;

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

    Extractor<GenericRecord, ?> extractor(int srcPos, Schema schema) {
        Extractor<GenericRecord, ?> e = extractorInternal(srcPos, schema.getFields().get(srcPos).schema());
        return compact ? e.compact() : e;
    }

    private Extractor<GenericRecord, ?> extractorInternal(int pos, Schema colSchema) {
        switch (colSchema.getType()) {
            // Raw numeric and boolean types can be loaded as primitives,
            // as numeric nullable types are declared as unions and will fall under the "default" case
            case INT:
                return Extractor.$int(r -> (Integer) r.get(pos));
            case DOUBLE:
                return Extractor.$double(r -> (Double) r.get(pos));
            case LONG:
                return Extractor.$long(r -> (Long) r.get(pos));
            case BOOLEAN:
                return Extractor.$bool(r -> (Boolean) r.get(pos));
            case BYTES:
            case ENUM:
            case NULL:
                return Extractor.$col(r -> r.get(pos));
            case STRING:
                return stringColumnExtractorInternal(pos);
            case UNION:
                return unionExtractorInternal(pos, colSchema.getTypes());
            default:
                throw new UnsupportedOperationException("(Yet) unsupported Avro schema type: " + colSchema.getType());
        }
    }

    private Extractor<GenericRecord, ?> unionExtractorInternal(int pos, List<Schema> types) {
        // we only know how to handle union with NULL

        Schema[] otherThanNull = types.stream().filter(t -> t.getType() != Schema.Type.NULL).toArray(Schema[]::new);
        if (otherThanNull.length != 1) {
            throw new IllegalStateException("Can't handle union type that is not ['something', null]: " + types);
        }

        boolean hasNull = types.size() > 1;
        if (!hasNull) {
            // allow primitives
            return extractorInternal(pos, otherThanNull[0]);
        }

        // don't allow primitives
        switch (otherThanNull[0].getType()) {
            case INT:
            case DOUBLE:
            case LONG:
            case BOOLEAN:
            case BYTES:
            case ENUM:
                return Extractor.$col(r -> r.get(pos));
            case STRING:
                return stringColumnExtractorInternal(pos);
            case UNION:
                return unionExtractorInternal(pos, otherThanNull[0].getTypes());
            default:
                throw new UnsupportedOperationException("(Yet) unsupported Avro schema type: " + otherThanNull[0].getType());
        }
    }

    private Extractor<GenericRecord, ?> stringColumnExtractorInternal(int pos) {

        // A few cases to handle:
        // 1. "String".equals(colSchema.getProp(GenericData.STRING_PROP)) -> String
        // 2. AvroTypeExtensions.UNMAPPED_TYPE.getName().equals(colSchema.getLogicalType().getName()) -> String
        // 3. All others: avro.util.Utf8 (which is mutable and requires an immediate conversion)
        // Luckily, all 3 can be converted to a String via "toString", so treating them the same...

        ValueMapper<GenericRecord, ?> mapper = r -> {
            Object val = r.get(pos);
            return val != null ? val.toString() : null;
        };

        return Extractor.$col(mapper);
    }
}
