package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimeUnit;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

class LocalDateTimeConverter extends StoringPrimitiveConverter<LocalDateTime> {

    public static LocalDateTimeConverter of(boolean accum, int accumCapacity, boolean allowsNulls, LogicalTypeAnnotation.TimeUnit timeUnit) {
        ValueStore<LocalDateTime> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new LocalDateTimeConverter(store, allowsNulls, timeUnit);
    }

    private final LongToLocalDateTime mapper;
    private LocalDateTime[] dict;

    public LocalDateTimeConverter(ValueStore<LocalDateTime> store, boolean allowsNulls, TimeUnit timeUnit) {
        super(store, allowsNulls);
        if (timeUnit == TimeUnit.MILLIS) {
            this.mapper = LocalDateTimeConverter::localDateTimeFromMillisFromEpoch;
        } else if (timeUnit == TimeUnit.MICROS) {
            this.mapper = LocalDateTimeConverter::localDateTimeFromMicrosFromEpoch;
        } else {
            this.mapper = LocalDateTimeConverter::localDateTimeFromNanosFromEpoch;
        }
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
    public boolean hasDictionarySupport() {
        return true;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        dict = new LocalDateTime[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = mapper.map(dictionary.decodeToLong(i));
        }
    }

    @FunctionalInterface
    private interface LongToLocalDateTime {
        LocalDateTime map(long timeFromEpoch);
    }

    private static LocalDateTime localDateTimeFromMillisFromEpoch(long millisFromEpoch) {
        Instant instant = Instants.fromEpochMillis(millisFromEpoch);
        return localDateTimeInUTC(instant);
    }

    private static LocalDateTime localDateTimeFromMicrosFromEpoch(long microsFromEpoch) {
        Instant instant = Instants.fromEpochMicros(microsFromEpoch);
        return localDateTimeInUTC(instant);
    }

    private static LocalDateTime localDateTimeFromNanosFromEpoch(long nanosFromEpoch) {
        Instant instant = Instants.fromEpochNanos(nanosFromEpoch);
        return localDateTimeInUTC(instant);
    }

    private static LocalDateTime localDateTimeInUTC(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

}
