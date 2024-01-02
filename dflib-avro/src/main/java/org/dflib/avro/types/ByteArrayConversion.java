package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;

/**
 * @since 0.11
 */
public class ByteArrayConversion extends SingleSchemaConversion<byte[]> {

    static final String NAME = "dflib-bytearray";

    public ByteArrayConversion() {
        super(NAME, Schema.Type.BYTES);
    }

    @Override
    public Class<byte[]> getConvertedType() {
        return byte[].class;
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
