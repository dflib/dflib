package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.LocalTime;


public class LocalTimeConversion extends SingleSchemaConversion<LocalTime> {

    static final String NAME = "dflib-localtime";

    public LocalTimeConversion() {
        super(NAME, Schema.Type.LONG);
    }

    @Override
    public Class<LocalTime> getConvertedType() {
        return LocalTime.class;
    }

    @Override
    public LocalTime fromLong(Long value, Schema schema, LogicalType type) {
        return LocalTime.ofNanoOfDay(value);
    }

    @Override
    public Long toLong(LocalTime value, Schema schema, LogicalType type) {
        return value.toNanoOfDay();
    }
}
