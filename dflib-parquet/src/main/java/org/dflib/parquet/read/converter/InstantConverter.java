package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.PrimitiveConverter;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimeUnit;
import org.dflib.parquet.read.converter.Instants.LongToInstant;

import java.time.Instant;
import java.util.function.Consumer;

class InstantConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;
    private final LongToInstant mapper;
    private Instant[] dict;

    public InstantConverter(Consumer<Object> consumer, TimeUnit timeUnit) {
        this.consumer = consumer;
        this.mapper = switch (timeUnit) {
            case MILLIS -> Instants::fromEpochMillis;
            case MICROS -> Instants::fromEpochMicros;
            case NANOS -> Instants::fromEpochNanos;
        };
    }

    @Override
    public void addLong(long timeToEpoch) {
        consumer.accept(mapper.map(timeToEpoch));
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
        dict = new Instant[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = mapper.map(dictionary.decodeToLong(i));
        }
    }

}
