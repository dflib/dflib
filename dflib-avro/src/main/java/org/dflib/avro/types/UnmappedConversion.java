package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

/**
 * A catch-all conversion for unmapped types that takes a kind of a CSV approach of serializing object as String,
 * and deserializing Strings instead of specific objects.
 */
public class UnmappedConversion extends SingleSchemaConversion<String> {

    public UnmappedConversion(SingleSchemaLogicalType type) {
        super(type);
    }

    @Override
    public Class<String> getConvertedType() {
        return String.class;
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
