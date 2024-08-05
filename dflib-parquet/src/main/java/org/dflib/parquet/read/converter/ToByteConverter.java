package org.dflib.parquet.read.converter;

import java.util.function.Consumer;

import org.apache.parquet.io.api.PrimitiveConverter;

class ToByteConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;

    public ToByteConverter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addInt(int value) {
        consumer.accept((byte) value);
    }

}
