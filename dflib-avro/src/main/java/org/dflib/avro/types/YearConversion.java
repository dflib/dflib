package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.Year;


public class YearConversion extends SingleSchemaConversion<Year> {

    static final String NAME = "dflib-year";

    public YearConversion() {
        super(NAME, Schema.Type.INT);
    }

    @Override
    public Class<Year> getConvertedType() {
        return Year.class;
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
