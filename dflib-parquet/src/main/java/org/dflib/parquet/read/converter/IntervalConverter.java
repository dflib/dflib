package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.PrimitiveConverter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

class IntervalConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;

    public IntervalConverter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addBinary(Binary value) {
        consumer.accept(convert(value));
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