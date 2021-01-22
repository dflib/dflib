package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

import java.nio.ByteBuffer;

class AvroDataFrameNormalizer {

    /**
     * Convert column values into types supported by Avro.
     */
    static DataFrame normalizeColumns(DataFrame df) {

        int w = df.width();
        for (int i = 0; i < w; i++) {

            // If a column has a mix of different types, these conversions are not going to be performed.
            // Which is probably fine, considering that Avro schema expects uniform columns.

            Series<?> series = df.getColumn(i);
            Class<?> type = series.getInferredType();
            String name = type.isArray() ? type.getComponentType().getName() + "[]" : type.getName();

            if ("byte[]".equals(name)) {
                df = df.convertColumn(i, AvroDataFrameNormalizer::toByteBuffer);
            }
        }

        return df;
    }

    private static ByteBuffer toByteBuffer(byte[] bytes) {
        return bytes != null ? ByteBuffer.wrap(bytes) : null;
    }
}
