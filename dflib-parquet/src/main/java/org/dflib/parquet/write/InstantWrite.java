package org.dflib.parquet.write;

import java.time.Instant;

import org.dflib.parquet.TimeUnit;

class InstantWrite {

    @FunctionalInterface
    public interface InstantToLong {
        long map(Instant instant);
    }

    static InstantToLong getInstantMapper(TimeUnit timeUnit) {
        switch (timeUnit) {
        case MILLIS:
            return InstantWrite::millisFromEpochFromInstant;
        case MICROS:
            return InstantWrite::microsFromEpochFromInstant;
        case NANOS:
            return InstantWrite::nanosFromEpochFromInstant;
        }
        throw new IllegalArgumentException("Invalid " + timeUnit);
    }

    static long millisFromEpochFromInstant(Instant instant) {
        return instant.toEpochMilli();
    }

    static long microsFromEpochFromInstant(Instant instant) {
        long seconds = instant.getEpochSecond();
        int nanos = instant.getNano();

        // Same implementation than Instant.toEpochMilli, but with 1_000_000L
        if (seconds < 0 && nanos > 0) {
            long micros = Math.multiplyExact(seconds + 1, 1_000_000L);
            long adjustment = (nanos / 1_000L) - 1_000_000L;
            return Math.addExact(micros, adjustment);
        } else {
            long micros = Math.multiplyExact(seconds, 1_000_000L);
            return Math.addExact(micros, nanos / 1_000L);
        }
    }

    static long nanosFromEpochFromInstant(Instant instant) {
        long seconds = instant.getEpochSecond();
        int nanos = instant.getNano();

        // Same implementation than Instant.toEpochMilli, but with 1_000_000_000L
        if (seconds < 0 && nanos > 0) {
            long micros = Math.multiplyExact(seconds + 1, 1_000_000_000L);
            long adjustment = nanos - 1_000_000_000L;
            return Math.addExact(micros, adjustment);
        } else {
            long micros = Math.multiplyExact(seconds, 1_000_000_000L);
            return Math.addExact(micros, nanos);
        }
    }
}
