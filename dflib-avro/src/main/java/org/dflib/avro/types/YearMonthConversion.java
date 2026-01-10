package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.YearMonth;


public class YearMonthConversion extends Conversion<YearMonth> {

    static final String NAME = "dflib-yearmonth";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.LONG);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.LONG));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<YearMonth> getConvertedType() {
        return YearMonth.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
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
        return (long) value.getYear() << 32 | value.getMonthValue() & 0xFFFFFFFFL;
    }
}
