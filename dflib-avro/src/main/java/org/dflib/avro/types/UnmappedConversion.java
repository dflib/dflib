package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

/**
 * A catch-all conversion for unmapped types that takes a kind of a CSV approach of serializing object as String,
 * and deserializing Strings instead of specific objects.
 */
public class UnmappedConversion extends Conversion<String> {

    public static final String NAME = "dflib-unmapped";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.STRING);
    public static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.STRING));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<String> getConvertedType() {
        return String.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
    }

    @Override
    public String fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return value.toString();
    }

    @Override
    public CharSequence toCharSequence(String value, Schema schema, LogicalType type) {
        return value;
    }
}
