package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;


public class ByteArrayConversion extends Conversion<byte[]> {

    static final String NAME = "dflib-bytearray";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.BYTES);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.BYTES));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<byte[]> getConvertedType() {
        return byte[].class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
    }

    @Override
    public byte[] fromBytes(ByteBuffer value, Schema schema, LogicalType type) {

        int pos = value.position();
        int limit = value.limit();
        byte[] bytes = new byte[limit - pos];

        value.get(bytes);

        return bytes;
    }

    @Override
    public ByteBuffer toBytes(byte[] value, Schema schema, LogicalType type) {
        return ByteBuffer.wrap(value);
    }
}
