package org.dflib.parquet.read.converter;

import java.util.function.Consumer;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.PrimitiveConverter;

class StringConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;
    private String[] dict = null;

    public StringConverter(Consumer<Object> consumer) {
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
        dict = new String[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToBinary(i));
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        consumer.accept(dict[dictionaryId]);
    }

    private String convert(Binary value) {
        return value.toStringUsingUTF8();
    }
}