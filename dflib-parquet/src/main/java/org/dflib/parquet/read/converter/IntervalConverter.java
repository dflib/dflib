package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Binary;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class IntervalConverter extends StoringPrimitiveConverter<int[]> {

    public static IntervalConverter of(boolean accum, int accumCapacity, boolean allowsNulls) {
        ValueStore<int[]> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new IntervalConverter(store, allowsNulls);
    }

    protected IntervalConverter(ValueStore<int[]> store, boolean allowsNulls) {
        super(store, false, allowsNulls);
    }

    @Override
    public void addBinary(Binary value) {
        store.push(convert(value));
    }

    private int[] convert(Binary value) {

        int[] ints = new int[3];
        ByteBuffer.wrap(value.getBytesUnsafe())
                .order(ByteOrder.LITTLE_ENDIAN)
                .asIntBuffer()
                .get(ints);

        return ints;
    }
}