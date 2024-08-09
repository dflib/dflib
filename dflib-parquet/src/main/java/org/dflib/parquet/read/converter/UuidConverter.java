package org.dflib.parquet.read.converter;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.PrimitiveConverter;

class UuidConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;
    private UUID[] dict = null;

    public UuidConverter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addBinary(Binary value) {
        consumer.accept(convert(value));
    }

    @Override
    public boolean hasDictionarySupport() {
        return true;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        dict = new UUID[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToBinary(i));
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        consumer.accept(dict[dictionaryId]);
    }

    private UUID convert(Binary value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(value.getBytes());
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }

}