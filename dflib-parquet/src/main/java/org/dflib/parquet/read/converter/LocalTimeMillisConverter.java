package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.PrimitiveConverter;

import java.time.LocalTime;
import java.util.function.Consumer;

class LocalTimeMillisConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;

    private LocalTime[] dict;

    public LocalTimeMillisConverter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addInt(int millisInDay) {
        consumer.accept(convert(millisInDay));
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        consumer.accept(dict[dictionaryId]);
    }

    @Override
    public boolean hasDictionarySupport() {
        return true;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        this.dict = new LocalTime[maxId + 1];

        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToInt(i));
        }
    }

    private LocalTime convert(int time) {
        return LocalTime.ofNanoOfDay(time * 1_000_000L);
    }

}
