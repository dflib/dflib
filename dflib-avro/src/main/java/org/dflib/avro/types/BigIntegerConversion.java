package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.math.BigInteger;
import java.nio.ByteBuffer;


public class BigIntegerConversion extends Conversion<BigInteger> {

    static final String NAME = "dflib-biginteger";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.BYTES);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.BYTES));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<BigInteger> getConvertedType() {
        return BigInteger.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
    }

    @Override
    public BigInteger fromBytes(ByteBuffer value, Schema schema, LogicalType type) {
        int len = value.limit() - value.position();
        byte[] bytes = new byte[len];
        value.get(bytes);
        return new BigInteger(bytes);
    }

    @Override
    public ByteBuffer toBytes(BigInteger value, Schema schema, LogicalType type) {
        return ByteBuffer.wrap(value.toByteArray());
    }
}
