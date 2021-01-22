package com.nhl.dflib.avro.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

import java.nio.ByteBuffer;
import java.time.LocalDate;

/**
 * "Normalizes" DataFrame columns to be able to save with Avro.
 *
 * @since 0.11
 */
public class DataFrameSaveNormalizer {

    public static DataFrame normalize(DataFrame df) {

        int w = df.width();
        for (int i = 0; i < w; i++) {

            // If a column has a mix of different types, these conversions are not going to be performed.
            // Which is probably fine, considering that Avro schema expects uniform columns.

            Series<?> series = df.getColumn(i);
            Class<?> type = series.getInferredType();
            String name = type.isArray() ? type.getComponentType().getName() + "[]" : type.getName();

            if ("byte[]".equals(name)) {
                df = df.convertColumn(i, DataFrameSaveNormalizer::normalizeByteArray);
            } else if ("java.time.LocalDate".equals(name)) {
                df = df.convertColumn(i, DataFrameSaveNormalizer::normalizeLocalDate);
            }
        }

        return df;
    }

    private static ByteBuffer normalizeByteArray(byte[] bytes) {
        return bytes != null ? ByteBuffer.wrap(bytes) : null;
    }

    private static int normalizeLocalDate(LocalDate date) {

        // per https://avro.apache.org/docs/current/spec.html#Date
        // "int stores the number of days from the unix epoch, 1 January 1970 (ISO calendar)"

        return date != null ? (int) date.toEpochDay() : -1;
    }
}
