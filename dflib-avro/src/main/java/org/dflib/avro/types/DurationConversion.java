package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;
import java.time.Duration;

public class DurationConversion extends SingleSchemaConversion<Duration> {

    static final String NAME = "dflib-duration";

    public DurationConversion() {
        super(NAME, Schema.Type.BYTES);
    }

    @Override
    public Class<Duration> getConvertedType() {
        return Duration.class;
    }

    @Override
    public Duration fromBytes(ByteBuffer value, Schema schema, LogicalType type) {

        long sec = value.getLong();
        int nano = value.getInt();

        return Duration.ofSeconds(sec, nano);
    }

    @Override
    public ByteBuffer toBytes(Duration value, Schema schema, LogicalType type) {

        ByteBuffer buffer = ByteBuffer.allocate(12);

        long secs = value.getSeconds();
        int nanos = value.getNano();

        buffer.putLong(secs);
        buffer.putInt(nanos);

        // must flip so that Avro can read the data we just put in
        buffer.flip();

        return buffer;
    }
}
