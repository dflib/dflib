package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.time.LocalTime;

class LocalTimeMicrosNanosConverter extends StoringPrimitiveConverter<LocalTime> {

    public static LocalTimeMicrosNanosConverter of(boolean accum, int accumCapacity, boolean dictionarySupport, boolean allowsNulls, LogicalTypeAnnotation.TimeUnit timeUnit) {
        ValueStore<LocalTime> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new LocalTimeMicrosNanosConverter(store, dictionarySupport, allowsNulls, timeUnit);
    }

    private final long factor;

    private LocalTime[] dict;

    protected LocalTimeMicrosNanosConverter(ValueStore<LocalTime> store, boolean dictionarySupport, boolean allowsNulls, LogicalTypeAnnotation.TimeUnit timeUnit) {
        super(store, dictionarySupport, allowsNulls);
        this.factor = switch (timeUnit) {
            case NANOS -> 1L;
            case MICROS -> 1_000L;
            default -> throw new IllegalArgumentException("Unexpected time unit: " + timeUnit);
        };
    }

    @Override
    public void addLong(long microsOrNanos) {
        store.push(convert(microsOrNanos));
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
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
