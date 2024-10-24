package org.dflib.avro;

import org.dflib.row.RowProxy;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

/**
 * A wrapper around RowProxy flyweight for efficient data copy from DataFrame to Avro. Also performs any needed type
 * conversions.
 */
class RowToAvroRecordAdapter implements GenericRecord {

    private final Schema schema;
    private RowProxy proxy;

    RowToAvroRecordAdapter(Schema schema) {
        this.schema = schema;
    }

    RowToAvroRecordAdapter resetRow(RowProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public void put(int i, Object v) {
        throw new UnsupportedOperationException("'put' is unsupported. This is a read-only record");
    }

    @Override
    public void put(String key, Object v) {
        throw new UnsupportedOperationException("'put' is unsupported. This is a read-only record");
    }

    @Override
    public Object get(String key) {
        return proxy.get(key);
    }

    @Override
    public Object get(int i) {
        return proxy.get(i);
    }

    @Override
    public Schema getSchema() {
        return schema;
    }
}
