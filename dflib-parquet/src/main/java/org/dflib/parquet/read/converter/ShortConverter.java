package org.dflib.parquet.read.converter;

import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class ShortConverter extends StoringPrimitiveConverter<Short> {

    public static ShortConverter of(boolean accum, int accumCapacity, boolean allowsNulls) {
        ValueStore<Short> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new ShortConverter(store, allowsNulls);
    }

    protected ShortConverter(ValueStore<Short> store, boolean allowsNulls) {
        super(store, false, allowsNulls);
    }

    @Override
    public void addInt(int value) {
        store.push((short) value);
    }
}
