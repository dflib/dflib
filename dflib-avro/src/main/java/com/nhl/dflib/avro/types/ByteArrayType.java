package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

public class ByteArrayType extends LogicalType {

    static final String NAME = "dflib-byte-array";

    public ByteArrayType() {
        super(NAME);
    }

    @Override
    public void validate(Schema schema) {
        super.validate(schema);

        if (schema.getType() != Schema.Type.BYTES) {
            throw new IllegalArgumentException("Logical type '" + NAME + "' must be backed by 'bytes'");
        }
    }
}
