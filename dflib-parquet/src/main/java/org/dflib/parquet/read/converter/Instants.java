package org.dflib.parquet.read.converter;

import java.time.Instant;

class Instants {

    @FunctionalInterface
    public interface LongToInstant {
        Instant map(long timeFromEpoch);
    }

    static Instant fromEpochMillis(long millisFromEpoch) {
        return Instant.ofEpochMilli(millisFromEpoch);
    }

    static Instant fromEpochMicros(long microsFromEpoch) {
        long secs = Math.floorDiv(microsFromEpoch, 1_000_000L);
        long nanos = Math.floorMod(microsFromEpoch, 1_000_000L) * 1000L;
        return Instant.ofEpochSecond(secs, nanos);
    }

    static Instant fromEpochNanos(long nanosFromEpoch) {
        long secs = Math.floorDiv(nanosFromEpoch, 1_000_000_000L);
        long nanos = Math.floorMod(nanosFromEpoch, 1_000_000_000L);
        return Instant.ofEpochSecond(secs, nanos);
    }

}
