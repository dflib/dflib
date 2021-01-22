package com.nhl.dflib.avro.loader;

import com.nhl.dflib.accumulator.Accumulator;

import java.nio.ByteBuffer;
import java.time.LocalDate;

/**
 * @since 0.11
 */
public class Accumulators {

    public static Accumulator<Object> forUtf8() {
        // Strings are served by Avro as flyweight Utf8 objects and must be converted
        return new ConvertingAccumulator<Object>(Accumulators::fromUtf8ToString);
    }

    public static Accumulator<Object> forByteBuffer() {
        // byte[]'s are served as flyweight ByteBuffer objects and must be converted
        return new ConvertingAccumulator<Object>(Accumulators::fromByteBufferToByteArray);
    }


    public static Accumulator<Object> forLocalDate() {
        // LocalDates are served as ints
        return new ConvertingAccumulator<Object>(Accumulators::fromIntToLocalDate);
    }

    private static String fromUtf8ToString(Object v) {
        return v.toString();
    }

    private static byte[] fromByteBufferToByteArray(Object v) {
        ByteBuffer buffer = (ByteBuffer) v;
        int pos = buffer.position();
        int limit = buffer.limit();
        byte[] bytes = new byte[limit - pos];

        buffer.get(bytes);

        return bytes;
    }

    private static LocalDate fromIntToLocalDate(Object v) {
        return LocalDate.ofEpochDay((Integer) v);
    }
}
