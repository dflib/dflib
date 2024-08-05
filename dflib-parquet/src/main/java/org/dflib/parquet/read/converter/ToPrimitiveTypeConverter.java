package org.dflib.parquet.read.converter;

import java.util.function.Consumer;

import org.apache.parquet.io.api.PrimitiveConverter;

class ToPrimitiveTypeConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;

    public ToPrimitiveTypeConverter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addLong(long value) {
        consumer.accept(value);
    }

    @Override
    public void addInt(int value) {
        consumer.accept(value);
    }

    @Override
    public void addFloat(float value) {
        consumer.accept(value);
    }

    @Override
    public void addDouble(double value) {
        consumer.accept(value);
    }

    @Override
    public void addBoolean(boolean value) {
        consumer.accept(value);
    }

}
