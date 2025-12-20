package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.PrimitiveConverter;

import java.util.function.Consumer;

class PrimitiveTypeConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;

    public PrimitiveTypeConverter(Consumer<Object> consumer) {
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

    @Override
    public void addBinary(Binary value) {
        consumer.accept(value.getBytesUnsafe());
    }
}
