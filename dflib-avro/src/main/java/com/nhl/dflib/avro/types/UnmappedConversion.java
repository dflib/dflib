package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

/**
 * A catch-all conversion for unmapped types that takes a kind of a CSV approach of serializing object as String,
 * and deserializing Strings instead of specific objects.
 *
 * @since 0.11
 */
public class UnmappedConversion extends SingleSchemaConversion<Object> {

    private static final String NAME = "dflib-unmapped";

    // stripping off the type generics, as this class intentionally doesn't follow the Avro contract
    // of serializing the same type as it deserializes.
    @SuppressWarnings("all")
    private final Class convertedType;

    public UnmappedConversion(Class<?> convertedType) {
        super(new SingleSchemaLogicalType(NAME, Schema.Type.STRING));
        this.convertedType = convertedType;
    }

    @Override
    @SuppressWarnings("all")
    public Class<Object> getConvertedType() {
        return convertedType;
    }

    @Override
    public Object fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        // we don't know how to deserialize the actual type, so just send back a String.
        return value.toString();
    }

    @Override
    public CharSequence toCharSequence(Object value, Schema schema, LogicalType type) {
        return value.toString();
    }
}
