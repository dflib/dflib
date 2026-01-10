package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;
import java.time.Duration;

public class DurationConversion extends Conversion<Duration> {

    static final String NAME = "dflib-duration";
    static final LogicalType TYPE = new SingleSchemaLogicalType(NAME, Schema.Type.BYTES);
    static final Schema RECOMMENDED_SCHEMA = TYPE.addToSchema(Schema.create(Schema.Type.BYTES));

    @Override
    public String getLogicalTypeName() {
        return NAME;
    }

    @Override
    public Class<Duration> getConvertedType() {
        return Duration.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return RECOMMENDED_SCHEMA;
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
