package org.dflib.parquet.read.converter;

import org.dflib.builder.BoolAccum;
import org.dflib.builder.BoolHolder;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class BoolConverter extends StoringPrimitiveConverter<Boolean> {

    public static BoolConverter of(boolean accum, int accumCapacity, boolean allowsNulls) {
        ValueStore<Boolean> store = allowsNulls
                ? (accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>())
                : (accum ? new BoolAccum(accumCapacity) : new BoolHolder());

        return new BoolConverter(store, allowsNulls);
    }

    protected BoolConverter(ValueStore<Boolean> store, boolean allowsNulls) {
        super(store, false, allowsNulls);
    }

    @Override
    public void addBoolean(boolean value) {
        store.pushBool(value);
    }
}
