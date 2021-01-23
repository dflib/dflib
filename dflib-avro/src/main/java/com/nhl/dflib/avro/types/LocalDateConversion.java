package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.LocalDate;

/**
 * @since 0.11
 */
public class LocalDateConversion extends SingleSchemaConversion<LocalDate> {

    static final String NAME = "dflib-local-date";

    public LocalDateConversion() {
        super(NAME, Schema.Type.INT);
    }

    @Override
    public Class<LocalDate> getConvertedType() {
        return LocalDate.class;
    }

    @Override
    public LocalDate fromInt(Integer value, Schema schema, LogicalType type) {
        return LocalDate.ofEpochDay(value);
    }

    @Override
    public Integer toInt(LocalDate value, Schema schema, LogicalType type) {
        return (int) value.toEpochDay();
    }
}
