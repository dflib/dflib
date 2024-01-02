package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.LocalDate;

/**
 * @since 0.11
 */
public class LocalDateConversion extends SingleSchemaConversion<LocalDate> {

    static final String NAME = "dflib-localdate";

    public LocalDateConversion() {
        super(NAME, Schema.Type.LONG);
    }

    @Override
    public Class<LocalDate> getConvertedType() {
        return LocalDate.class;
    }

    @Override
    public LocalDate fromLong(Long value, Schema schema, LogicalType type) {
        return LocalDate.ofEpochDay(value);
    }

    @Override
    public Long toLong(LocalDate value, Schema schema, LogicalType type) {
        return value.toEpochDay();
    }
}
