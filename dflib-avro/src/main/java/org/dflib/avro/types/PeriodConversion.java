package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;
import java.time.Period;

public class PeriodConversion extends Conversion<Period> {

    static final String NAME = "dflib-period";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.BYTES);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.BYTES));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<Period> getConvertedType() {
        return Period.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
    }

    @Override
    public Period fromBytes(ByteBuffer value, Schema schema, LogicalType type) {

        int years = value.getInt();
        int months = value.getInt();
        int days = value.getInt();

        return Period.of(years, months, days);
    }

    @Override
    public ByteBuffer toBytes(Period value, Schema schema, LogicalType type) {

        ByteBuffer buffer = ByteBuffer.allocate(12);

        int years = value.getYears();
        int months = value.getMonths();
        int days = value.getDays();

        buffer.putInt(years);
        buffer.putInt(months);
        buffer.putInt(days);

        // must flip so that Avro can read the data we just put in
        buffer.flip();

        return buffer;
    }
}
