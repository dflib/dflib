package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Binary;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class BytesConverter extends StoringPrimitiveConverter<byte[]> {

    public static BytesConverter of(boolean accum, int accumCapacity, boolean allowsNulls) {
        ValueStore<byte[]> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new BytesConverter(store, allowsNulls);
    }

    protected BytesConverter(ValueStore<byte[]> store, boolean allowsNulls) {
        super(store, allowsNulls);
    }

    @Override
    public void addBinary(Binary value) {
        // TODO: unsafe ?? does it work in a multi-row sceanrio
        store.push(value.getBytesUnsafe());
    }
}
