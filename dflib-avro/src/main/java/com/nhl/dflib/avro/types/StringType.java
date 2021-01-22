package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

public class StringType extends LogicalType {

    static final String NAME = "dflib-string";

    public StringType() {
        super(NAME);
    }

    @Override
    public void validate(Schema schema) {
        super.validate(schema);

        if (schema.getType() != Schema.Type.STRING) {
            throw new IllegalArgumentException("Logical type '" + NAME + "' must be backed by 'string'");
        }
    }
}