package org.dflib.parquet.write;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.dflib.parquet.TimeUnit;

class LocalDateTimeWrite {

    @FunctionalInterface
    public interface LocalDateTimeToLong {
        long map(LocalDateTime localdateTime);
    }

    public static LocalDateTimeToLong getLocalDateTimeMapper(TimeUnit timeUnit) {
        switch (timeUnit) {
        case MILLIS:
            return LocalDateTimeWrite::millisFromEpochFromLocalDateTime;
        case MICROS:
            return LocalDateTimeWrite::microsFromEpochFromLocalDateTime;
        case NANOS:
            return LocalDateTimeWrite::nanosFromEpochFromLocalDateTime;
        }
        throw new IllegalArgumentException("Invalid " + timeUnit);
    }

    private static long millisFromEpochFromLocalDateTime(LocalDateTime localDateTime) {
        Instant instant = timestampInUTCOffset(localDateTime);
        return InstantWrite.millisFromEpochFromInstant(instant);
    }

    private static long microsFromEpochFromLocalDateTime(LocalDateTime localDateTime) {
        Instant instant = timestampInUTCOffset(localDateTime);
        return InstantWrite.microsFromEpochFromInstant(instant);
    }

    private static long nanosFromEpochFromLocalDateTime(LocalDateTime localDateTime) {
        Instant instant = timestampInUTCOffset(localDateTime);
        return InstantWrite.nanosFromEpochFromInstant(instant);
    }

    private static Instant timestampInUTCOffset(LocalDateTime timestamp) {
        return timestamp.toInstant(ZoneOffset.UTC);
    }

}
