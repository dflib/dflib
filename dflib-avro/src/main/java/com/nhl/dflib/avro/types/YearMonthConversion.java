package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.YearMonth;

/**
 * @since 0.11
 */
public class YearMonthConversion extends SingleSchemaConversion<YearMonth> {

    static final String NAME = "dflib-yearmonth";

    public YearMonthConversion() {
        super(NAME, Schema.Type.LONG);
    }

    @Override
    public Class<YearMonth> getConvertedType() {
        return YearMonth.class;
    }

    @Override
    public YearMonth fromLong(Long value, Schema schema, LogicalType type) {
        long v = value;
        int year = (int) (v >> 32);
        int month = (int) v;

        return YearMonth.of(year, month);
    }

    @Override
    public Long toLong(YearMonth value, Schema schema, LogicalType type) {
        return  (long) value.getYear() << 32 | value.getMonthValue() & 0xFFFFFFFFL;
    }
}
