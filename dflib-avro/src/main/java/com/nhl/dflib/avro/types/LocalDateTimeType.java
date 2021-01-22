package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

/**
 * @since 0.11
 */
public class LocalDateTimeType extends LogicalType {

    static final String NAME = "dflib-local-date-time";

    public LocalDateTimeType() {
        super(NAME);
    }

    @Override
    public void validate(Schema schema) {
        super.validate(schema);

        // Can't fit nanosecond precision into a "long" (it overflows some time after the year 2200)
        // So using "bytes" type instead

        if (schema.getType() != Schema.Type.BYTES) {
            throw new IllegalArgumentException("Logical type '" + NAME + "' must be backed by 'bytes'");
        }
    }
}
