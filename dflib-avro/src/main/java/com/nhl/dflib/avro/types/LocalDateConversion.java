package com.nhl.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.LocalDate;

public class LocalDateConversion extends Conversion<LocalDate> {

    @Override
    public Class<LocalDate> getConvertedType() {
        return LocalDate.class;
    }

    @Override
    public String getLogicalTypeName() {
        return LocalDateType.NAME;
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
