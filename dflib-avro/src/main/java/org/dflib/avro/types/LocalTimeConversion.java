package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.LocalTime;

/**
 * @deprecated as we can use standard "time-micros" or "time-millis" logical type for LocalTime encoding. Preserved for
 * decoding Avro files created with DFLib 1.x.
 */
@Deprecated(since = "2.0.0")
public class LocalTimeConversion extends Conversion<LocalTime> {

    static final String NAME = "dflib-localtime";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.LONG);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.LONG));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<LocalTime> getConvertedType() {
        return LocalTime.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
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
