package org.dflib.parquet.read.converter;

import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class ByteConverter extends StoringPrimitiveConverter<Byte> {

    public static ByteConverter of(boolean accum, int accumCapacity, boolean allowNulls) {
        ValueStore<Byte> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new ByteConverter(store, allowNulls);
    }

    protected ByteConverter(ValueStore<Byte> store, boolean allowsNulls) {
        super(store, allowsNulls);
    }

    @Override
    public void addInt(int value) {
        store.push((byte) value);
    }
}
