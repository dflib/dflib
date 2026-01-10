package org.dflib.avro.write;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Encoder;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @since 2.0.0
 */
public class GenericRecordWriter extends GenericDatumWriter<GenericRecord> {

    public GenericRecordWriter(Schema root, GenericData data) {
        super(root, data);
    }

    @Override
    protected void writeBytes(Object datum, Encoder out) throws IOException {
        Object wrapped = datum instanceof byte[] ba ? ByteBuffer.wrap(ba) : datum;
        super.writeBytes(wrapped, out);
    }
}
