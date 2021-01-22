package com.nhl.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

/**
 * @since 0.11
 */
public class StringConversion extends Conversion<String> {

    @Override
    public Class<String> getConvertedType() {
        return String.class;
    }

    @Override
    public String getLogicalTypeName() {
        return StringType.NAME;
    }

    @Override
    public String fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return value != null ? value.toString() : null;
    }

    @Override
    public CharSequence toCharSequence(String value, Schema schema, LogicalType type) {
        return value;
    }
}
