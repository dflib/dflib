package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.PrimitiveConverter;
import org.apache.parquet.schema.LogicalTypeAnnotation;

import java.time.LocalTime;
import java.util.function.Consumer;

class LocalTimeMicrosNanosConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;
    private final long factor;

    private LocalTime[] dict;

    public LocalTimeMicrosNanosConverter(Consumer<Object> consumer, LogicalTypeAnnotation.TimeUnit timeUnit) {
        this.consumer = consumer;
        this.factor = switch (timeUnit) {
            case NANOS -> 1L;
            case MICROS -> 1_000L;
            default -> throw new IllegalArgumentException("Unexpected time unit: " + timeUnit);
        };
    }

    @Override
    public void addLong(long microsOrNanos) {
        consumer.accept(convert(microsOrNanos));
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
        dict = new LocalTime[maxId + 1];

        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToLong(i));
        }
    }

    private LocalTime convert(long time) {
        return LocalTime.ofNanoOfDay(time * factor);
    }
}
