package org.dflib.parquet.read.converter;

import java.time.LocalTime;
import java.util.function.Consumer;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.PrimitiveConverter;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimeUnit;

class LocalTimeConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;
    private final long factor;
    private final TimeUnit timeUnit;
    private LocalTime[] dict = null;

    public LocalTimeConverter(Consumer<Object> consumer, TimeUnit timeUnit) {
        this.consumer = consumer;
        this.timeUnit = timeUnit;
        if (timeUnit == TimeUnit.MILLIS) {
            this.factor = 1_000_000L;
        } else if (timeUnit == TimeUnit.MICROS) {
            this.factor = 1000L;
        } else {
            this.factor = 1L;
        }
    }

    @Override
    public void addInt(int millisInDay) {
        consumer.accept(convert(millisInDay));
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
        if (timeUnit == TimeUnit.MILLIS) {
            for (int i = 0; i <= maxId; i++) {
                dict[i] = convert(dictionary.decodeToInt(i));
            }
        } else {
            for (int i = 0; i <= maxId; i++) {
                dict[i] = convert(dictionary.decodeToLong(i));
            }
        }
    }

    private LocalTime convert(long time) {
        return LocalTime.ofNanoOfDay(time * factor);
    }

}
