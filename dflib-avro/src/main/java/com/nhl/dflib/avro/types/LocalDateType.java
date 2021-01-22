package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

public class LocalDateType extends LogicalType {

    static final String NAME = "dflib-local-date";

    public LocalDateType() {
        super(NAME);
    }

    @Override
    public void validate(Schema schema) {
        super.validate(schema);

        if (schema.getType() != Schema.Type.INT) {
            throw new IllegalArgumentException("Logical type '" + NAME + "' must be backed by 'int'");
        }
    }
}
