package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * @deprecated as we can use a standard "decimal" logical type for BigDecimal encoding. Preserved for decoding Avro files
 * created with DFLib 1.x.
 */
@Deprecated(since = "2.0.0")
public class BigDecimalConversion extends Conversion<BigDecimal> {

    static final String NAME = "dflib-bigdecimal";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.BYTES);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.BYTES));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<BigDecimal> getConvertedType() {
        return BigDecimal.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
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
