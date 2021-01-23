package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * @since 0.11
 */
// Avro has its own "decimal" logical type mapping to BigDecimal. It is using "fixed" simple type and mandates
// scale and precision. We are using ByteBuffer serialization and arbitrary precision. Is there any advantage
// to "fixed" vs "binary" type?
public class BigDecimalConversion extends SingleSchemaConversion<BigDecimal> {

    static final String NAME = "dflib-bigdecimal";

    public BigDecimalConversion() {
        super(NAME, Schema.Type.BYTES);
    }

    @Override
    public Class<BigDecimal> getConvertedType() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal fromBytes(ByteBuffer value, Schema schema, LogicalType type) {
        int scale = value.getInt();
        int len = value.getInt();

        byte[] numbers = new byte[len];
        value.get(numbers);

        return new BigDecimal(new BigInteger(numbers), scale);
    }

    @Override
    public ByteBuffer toBytes(BigDecimal value, Schema schema, LogicalType type) {
        byte[] numbers = value.unscaledValue().toByteArray();
        int scale = value.scale();

        ByteBuffer buffer = ByteBuffer.allocate(numbers.length + 8);
        buffer.putInt(scale);
        buffer.putInt(numbers.length);
        buffer.put(numbers);

        // must flip so that Avro can read the data we just put in
        buffer.flip();

        return buffer;
    }
}
