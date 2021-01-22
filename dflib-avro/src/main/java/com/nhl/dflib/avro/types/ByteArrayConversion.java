package com.nhl.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;

public class ByteArrayConversion extends Conversion<byte[]> {

    @Override
    public Class<byte[]> getConvertedType() {
        return byte[].class;
    }

    @Override
    public String getLogicalTypeName() {
        return ByteArrayType.NAME;
    }

    @Override
    public byte[] fromBytes(ByteBuffer value, Schema schema, LogicalType type) {

        if (value == null) {
            return null;
        }

        int pos = value.position();
        int limit = value.limit();
        byte[] bytes = new byte[limit - pos];

        value.get(bytes);

        return bytes;
    }

    @Override
    public ByteBuffer toBytes(byte[] value, Schema schema, LogicalType type) {
        return value != null ? ByteBuffer.wrap(value) : null;
    }
}
