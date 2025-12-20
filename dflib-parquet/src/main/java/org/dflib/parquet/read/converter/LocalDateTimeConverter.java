package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.PrimitiveConverter;
import org.apache.parquet.schema.LogicalTypeAnnotation.TimeUnit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

class LocalDateTimeConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;
    private final LongToLocalDateTime mapper;
    private LocalDateTime[] dict;

    public LocalDateTimeConverter(Consumer<Object> consumer, TimeUnit timeUnit) {
        this.consumer = consumer;
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
