package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.LocalDate;


/**
 * @deprecated as we can use a standard "date" logical type for LocalDate encoding. Preserved for decoding Avro files
 * created with DFLib 1.x.
 */
@Deprecated(since = "2.0.0")
public class LocalDateConversion extends Conversion<LocalDate> {

    static final String NAME = "dflib-localdate";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.LONG);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.LONG));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<LocalDate> getConvertedType() {
        return LocalDate.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
    }

    @Override
    public LocalDate fromLong(Long value, Schema schema, LogicalType type) {
        return LocalDate.ofEpochDay(value);
    }

    @Override
    public Long toLong(LocalDate value, Schema schema, LogicalType type) {
        return value.toEpochDay();
    }
}
