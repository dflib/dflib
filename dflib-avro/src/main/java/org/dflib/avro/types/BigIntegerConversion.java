package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.math.BigInteger;
import java.nio.ByteBuffer;


public class BigIntegerConversion extends SingleSchemaConversion<BigInteger> {

    static final String NAME = "dflib-biginteger";

    public BigIntegerConversion() {
        super(NAME, Schema.Type.BYTES);
    }

    @Override
    public Class<BigInteger> getConvertedType() {
        return BigInteger.class;
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
