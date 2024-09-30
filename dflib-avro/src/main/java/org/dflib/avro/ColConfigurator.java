package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.ValueMapper;
import org.dflib.avro.schema.AvroSchemaUtils;

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
        Extractor<GenericRecord, ?> e = sparseExtractor(srcPos, schema.getFields().get(srcPos).schema());
        return compact ? e.compact() : e;
    }

    private static Extractor<GenericRecord, ?> sparseExtractor(int pos, Schema colSchema) {
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
            case NULL:
                return Extractor.$col(r -> r.get(pos));
            case ENUM:
                return enumExtractor(pos, colSchema);
            case STRING:
                return stringExtractor(pos);
            case UNION:
                return unionExtractor(pos, colSchema.getTypes());
            default:
                throw new UnsupportedOperationException("(Yet) unsupported Avro schema type: " + colSchema.getType());
        }
    }

    private static Extractor<GenericRecord, ?> unionExtractor(int pos, List<Schema> types) {
        // we only know how to handle union with NULL

        Schema[] otherThanNull = types.stream().filter(t -> t.getType() != Schema.Type.NULL).toArray(Schema[]::new);
        if (otherThanNull.length != 1) {
            throw new IllegalStateException("Can't handle union type that is not ['something', null]: " + types);
        }

        boolean hasNull = types.size() > 1;
        if (!hasNull) {
            // allow primitives
            return sparseExtractor(pos, otherThanNull[0]);
        }

        // don't allow primitives
        switch (otherThanNull[0].getType()) {
            case INT:
            case DOUBLE:
            case LONG:
            case BOOLEAN:
            case BYTES:
                return Extractor.$col(r -> r.get(pos));
            case ENUM:
                return enumExtractor(pos, otherThanNull[0]);
            case STRING:
                return stringExtractor(pos);
            case UNION:
                return unionExtractor(pos, otherThanNull[0].getTypes());
            default:
                throw new UnsupportedOperationException("(Yet) unsupported Avro schema type: " + otherThanNull[0].getType());
        }
    }

    private static Extractor<GenericRecord, ?> enumExtractor(int pos, Schema colSchema) {

        // GenericEnumSymbols are converted to enums if possible, or to Strings if not
        // (when the class is not known in the deserialization env)

        Class<Enum> enumType = AvroSchemaUtils.knownEnumType(colSchema);
        ValueMapper<GenericRecord, ?> mapper = enumType != null
                ? r -> asEnum(enumType, r.get(pos))
                : r -> asString(r.get(pos));
        return Extractor.$col(mapper);
    }

    private static Extractor<GenericRecord, ?> stringExtractor(int pos) {

        // A few cases to handle:
        // 1. "String".equals(colSchema.getProp(GenericData.STRING_PROP)) -> String
        // 2. AvroTypeExtensions.UNMAPPED_TYPE.getName().equals(colSchema.getLogicalType().getName()) -> String
        // 3. All others: avro.util.Utf8 (which is mutable and requires an immediate conversion)
        // Luckily, all 3 can be converted to a String via "toString", so treating them the same...
        
        return Extractor.$col(r -> asString(r.get(pos)));
    }

    private static <T extends Enum<T>> T asEnum(Class<T> enumType, Object val) {
        return val != null ? Enum.valueOf(enumType, val.toString()) : null;
    }

    private static String asString(Object val) {
        return val != null ? val.toString() : null;
    }
}
