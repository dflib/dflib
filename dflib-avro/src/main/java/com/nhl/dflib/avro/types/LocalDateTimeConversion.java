package com.nhl.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @since 0.11
 */
public class LocalDateTimeConversion extends Conversion<LocalDateTime> {

    @Override
    public Class<LocalDateTime> getConvertedType() {
        return LocalDateTime.class;
    }

    @Override
    public String getLogicalTypeName() {
        return LocalDateTimeType.NAME;
    }

    @Override
    public LocalDateTime fromBytes(ByteBuffer value, Schema schema, LogicalType type) {

        ByteBuffer reset = value.duplicate();
        long sec = reset.getLong();
        int nano = reset.getInt();

        return LocalDateTime.ofEpochSecond(sec, nano, ZoneOffset.UTC);
    }

    @Override
    public ByteBuffer toBytes(LocalDateTime value, Schema schema, LogicalType type) {

        ByteBuffer buffer = ByteBuffer.allocate(12);

        long secs = value.toEpochSecond(ZoneOffset.UTC);
        int nanos = value.getNano();

        buffer.putLong(secs);
        buffer.putInt(nanos);

        // must flip so that Avro can read the data we just put in
        buffer.flip();

        return buffer;
    }
}
