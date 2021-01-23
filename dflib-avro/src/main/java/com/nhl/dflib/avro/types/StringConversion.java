package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

/**
 * @since 0.11
 */
public class StringConversion extends SingleSchemaConversion<String> {

    static final String NAME = "dflib-string";

    public StringConversion() {
        super(NAME, Schema.Type.STRING);
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
