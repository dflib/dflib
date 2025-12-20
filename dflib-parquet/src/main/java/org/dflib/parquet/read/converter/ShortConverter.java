package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.PrimitiveConverter;

import java.util.function.Consumer;

class ShortConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;

    public ShortConverter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addInt(int value) {
        consumer.accept((short) value);
    }

}
