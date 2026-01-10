package org.dflib.avro.schema;

import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

/**
 * A time unit for rounding times and timestamps when generating schema and saving to Avro
 *
 * @since 2.0.0
 */
public enum SchemaTimeUnit {

    MILLIS(
            LogicalTypes.timeMillis().addToSchema(Schema.create(Schema.Type.INT)),
            LogicalTypes.localTimestampMillis().addToSchema(Schema.create(Schema.Type.LONG)),
            LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG))),

    MICROS(
            LogicalTypes.timeMicros().addToSchema(Schema.create(Schema.Type.LONG)),
            LogicalTypes.localTimestampMicros().addToSchema(Schema.create(Schema.Type.LONG)),
            LogicalTypes.timestampMicros().addToSchema(Schema.create(Schema.Type.LONG))),

    NANOS(
            // TODO: NANOS are not supported for times in the spec v 1.12. Why?
            LogicalTypes.timeMicros().addToSchema(Schema.create(Schema.Type.LONG)),
            LogicalTypes.localTimestampNanos().addToSchema(Schema.create(Schema.Type.LONG)),
            LogicalTypes.timestampNanos().addToSchema(Schema.create(Schema.Type.LONG)));

    public final Schema timeSchema;
    public final Schema localTimestampSchema;
    public final Schema timestampSchema;

    SchemaTimeUnit(Schema timeSchema, Schema localTimestampSchema, Schema timestampSchema) {
        this.timeSchema = timeSchema;
        this.localTimestampSchema = localTimestampSchema;
        this.timestampSchema = timestampSchema;
    }
}
