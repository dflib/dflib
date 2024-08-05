package org.dflib.parquet.read.converter;

import java.time.LocalDate;
import java.util.function.Consumer;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.PrimitiveConverter;

class LocalDateConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;
    private LocalDate[] dict = null;

    public LocalDateConverter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addInt(int daysFromEpoch) {
        consumer.accept(convert(daysFromEpoch));
    }

    @Override
    public boolean hasDictionarySupport() {
        return true;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        dict = new LocalDate[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToInt(i));
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        consumer.accept(dict[dictionaryId]);
    }

    private LocalDate convert(int daysFromEpoch) {
        return LocalDate.ofEpochDay(daysFromEpoch);
    }

}
