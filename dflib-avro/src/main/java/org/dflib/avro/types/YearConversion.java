package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.Year;


public class YearConversion extends Conversion<Year> {

    static final String NAME = "dflib-year";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.INT);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.INT));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<Year> getConvertedType() {
        return Year.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
    }

    @Override
    public Year fromInt(Integer value, Schema schema, LogicalType type) {
        return Year.of(value);
    }

    @Override
    public Integer toInt(Year value, Schema schema, LogicalType type) {
        return value.getValue();
    }
}
