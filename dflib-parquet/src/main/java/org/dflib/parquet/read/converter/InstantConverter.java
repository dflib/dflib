package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimeUnit;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;
import org.dflib.parquet.read.converter.Instants.LongToInstant;

import java.time.Instant;

class InstantConverter extends StoringPrimitiveConverter<Instant> {

    public static InstantConverter of(boolean accum, int accumCapacity, boolean dictionarySupport, boolean allowsNulls, LogicalTypeAnnotation.TimeUnit timeUnit) {
        ValueStore<Instant> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new InstantConverter(store, dictionarySupport, allowsNulls, timeUnit);
    }

    private final LongToInstant mapper;
    private Instant[] dict;

    protected InstantConverter(ValueStore<Instant> store, boolean dictionarySupport, boolean allowsNulls, TimeUnit timeUnit) {
        super(store, dictionarySupport, allowsNulls);
        this.mapper = switch (timeUnit) {
            case MILLIS -> Instants::fromEpochMillis;
            case MICROS -> Instants::fromEpochMicros;
            case NANOS -> Instants::fromEpochNanos;
        };
    }

    @Override
    public void addLong(long timeToEpoch) {
        store.push(mapper.map(timeToEpoch));
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
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
