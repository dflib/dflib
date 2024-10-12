package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class LocalDateTimeConversion extends SingleSchemaConversion<LocalDateTime> {

    static final String NAME = "dflib-localdatetime";

    public LocalDateTimeConversion() {
        super(NAME, Schema.Type.BYTES);
    }

    @Override
    public Class<LocalDateTime> getConvertedType() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime fromBytes(ByteBuffer value, Schema schema, LogicalType type) {

        long sec = value.getLong();
        int nano = value.getInt();

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
