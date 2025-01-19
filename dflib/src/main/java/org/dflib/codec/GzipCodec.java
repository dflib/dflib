package org.dflib.codec;

import org.dflib.ByteSource;

import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

/**
 * @since 2.0.0
 */
public class GzipCodec implements Codec {

    @Override
    public ByteSource decompress(ByteSource compressed) {

        Objects.requireNonNull(compressed);

        return () -> {
            try {
                return new GZIPInputStream(compressed.stream());
            } catch (IOException e) {
                throw new RuntimeException("Error creating GZIP stream", e);
            }
        };
    }
}
