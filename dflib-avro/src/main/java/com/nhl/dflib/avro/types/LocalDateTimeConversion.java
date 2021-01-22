package com.nhl.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeConversion extends Conversion<LocalDateTime> {

    @Override
    public Class<LocalDateTime> getConvertedType() {
        return LocalDateTime.class;
    }

    @Override
    public String getLogicalTypeName() {
        return LocalDateTimeType.NAME;
    }

    @Override
    public LocalDateTime fromLong(Long value, Schema schema, LogicalType type) {

        long lvalue = value;
        long sec = lvalue / 1_000_000_000;
        long nano = lvalue % 1_000_000_000;

        return LocalDateTime.ofEpochSecond(sec, (int) nano, ZoneOffset.UTC);
    }

    @Override
    public Long toLong(LocalDateTime value, Schema schema, LogicalType type) {

        long secs = value.toEpochSecond(ZoneOffset.UTC);

        // TODO: a date way in the future may overflow MAX_LONG
        return secs * 1_000_000_000 + value.getNano();
    }
}
