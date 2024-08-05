package org.dflib.parquet.read.converter;

import java.util.function.Consumer;

import org.apache.parquet.io.api.PrimitiveConverter;

class ToShortConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;

    public ToShortConverter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addInt(int value) {
        consumer.accept((short) value);
    }

}
