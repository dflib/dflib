package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.ValueMapper;
import org.dflib.avro.schema.AvroSchemaUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // starting with a "no-nulls" assumption until we stumble on a "union" type with nulls
        Extractor<GenericRecord, ?> e = sparseExtractor(srcPos, schema.getFields().get(srcPos).schema());
        return compact ? e.compact() : e;
    }

    private static Extractor<GenericRecord, ?> sparseExtractor(int pos, Schema schema) {

        if (schema.getLogicalType() != null) {
            return Extractor.$col(r -> r.get(pos));
        }

        return switch (schema.getType()) {

            // primitives support
            case INT -> Extractor.$int(r -> (Integer) r.get(pos));
            case FLOAT -> Extractor.$float(r -> (Float) r.get(pos));
            case DOUBLE -> Extractor.$double(r -> (Double) r.get(pos));
            case LONG -> Extractor.$long(r -> (Long) r.get(pos));
            case BOOLEAN -> Extractor.$bool(r -> (Boolean) r.get(pos));

            default -> {
                ValueMapper<Object, ?> converter = valueConverter(schema);
                yield Extractor.$col(r -> converter.map(r.get(pos)));
            }
        };
    }

    private static ValueMapper<Object, ?> valueConverter(Schema schema) {

        if (schema.getLogicalType() != null) {
            return v -> v;
        }

        return switch (schema.getType()) {
            case INT, FLOAT, DOUBLE, LONG, BOOLEAN, NULL -> v -> v;
            case STRING -> asString();
            case BYTES -> asBytes();
            case ENUM -> asEnum(schema);
            case UNION -> unwrapUnion(schema);
            case FIXED -> asBytesFromFixed();
            case ARRAY -> asList(schema);
            case MAP -> asMap(schema);
            case RECORD -> asRecord(schema);
        };
    }

    private static ValueMapper<Object, ?> asEnum(Schema schema) {
        Class<Enum> enumType = AvroSchemaUtils.knownEnumType(schema);
        return enumType != null
                ? v -> v != null ? Enum.valueOf(enumType, v.toString()) : null
                : v -> v != null ? v.toString() : null;
    }

    private static ValueMapper<Object, ?> asString() {

        // A few cases to handle:
        // 1. "String".equals(colSchema.getProp(GenericData.STRING_PROP)) -> String
        // 2. AvroTypeExtensions.UNMAPPED_TYPE.getName().equals(colSchema.getLogicalType().getName()) -> String
        // 3. All others: avro.util.Utf8 (which is mutable and requires an immediate conversion)
        // Luckily, all 3 can be converted to a String via "toString", so treating them the same...
        return v -> v != null ? v.toString() : null;
    }

    private static ValueMapper<Object, ?> unwrapUnion(Schema schema) {

        Schema[] otherThanNull = schema.getTypes().stream().filter(t -> t.getType() != Schema.Type.NULL).toArray(Schema[]::new);
        if (otherThanNull.length != 1) {
            // TODO: support all kinds of unions, not just ["t", "null"] and ["null", "t"]
            throw new IllegalStateException("Can't handle union type that is not ['something', null]: " + schema.getTypes());
        }

        // TODO: we are not taking advantage of the knowledge that the "schema" is nullable
        //  (vs. others that are not nullable). E.g., that would allow nested primitive types and skipping some null
        //  checking

        return valueConverter(otherThanNull[0]);
    }

    private static ValueMapper<Object, ?> asBytes() {
        return v -> {
            if (v instanceof ByteBuffer bb) {
                byte[] bytes = new byte[bb.remaining()];
                bb.duplicate().get(bytes);
                return bytes;
            } else {
                return null;
            }
        };
    }

    private static ValueMapper<Object, ?> asBytesFromFixed() {
        return v -> {

            if (v instanceof GenericData.Fixed f) {
                // must copy the bytes, as GenericData.Fixed is a flyweight
                byte[] tmpBytes = f.bytes();
                byte[] bytes = new byte[tmpBytes.length];
                System.arraycopy(tmpBytes, 0, bytes, 0, tmpBytes.length);
                return bytes;
            } else {
                return null;
            }
        };
    }

    private static ValueMapper<Object, ?> asList(Schema schema) {
        ValueMapper<Object, ?> elementConverter = valueConverter(schema.getElementType());
        return v -> {

            if (v instanceof GenericData.Array a) {
                List<Object> result = new ArrayList<>(a.size());
                for (Object e : a) {
                    result.add(elementConverter.map(e));
                }

                return result;
            } else {
                return null;
            }
        };
    }

    private static ValueMapper<Object, ?> asMap(Schema schema) {
        ValueMapper<Object, ?> elementConverter = valueConverter(schema.getValueType());
        return v -> {

            if (v instanceof Map m) {
                Map<String, Object> result = new HashMap<>((int) Math.ceil(m.size() / 0.75));
                m.forEach((k, val) -> result.put(k.toString(), elementConverter.map(val)));
                return result;
            } else {
                return null;
            }
        };
    }

    private static ValueMapper<Object, ?> asRecord(Schema schema) {

        Schema.Field[] fields = schema.getFields().toArray(new Schema.Field[0]);
        int w = fields.length;
        int capacity = (int) Math.ceil(w / 0.75);
        ValueMapper<Object, ?>[] converters = new ValueMapper[w];
        for (int i = 0; i < w; i++) {
            converters[i] = valueConverter(fields[i].schema());
        }

        return v -> {
            if (v instanceof GenericData.Record r) {
                Map<String, Object> result = new HashMap<>(capacity);

                for (int i = 0; i < w; i++) {
                    result.put(fields[i].name(), converters[i].map(r.get(i)));
                }

                return result;
            } else {
                return null;
            }
        };
    }
}
