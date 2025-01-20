package org.dflib.codec;

import org.dflib.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @since 2.0.0
 */
public class GzipCodec implements Codec {

    @Override
    public ByteSource decompress(ByteSource compressed) {

        Objects.requireNonNull(compressed);

        return new ByteSource() {

            @Override
            public Optional<String> uri() {
                return compressed.uri();
            }

            @Override
            public InputStream stream() {
                try {
                    return new GZIPInputStream(compressed.stream());
                } catch (IOException e) {
                    throw new RuntimeException("Error creating GZIP stream", e);
                }
            }
        };
    }

    @Override
    public OutputStream compress(OutputStream uncompressed) {
        try {
            return new GZIPOutputStream(uncompressed);
        } catch (IOException e) {
            throw new RuntimeException("Error creating GZIP stream", e);
        }
    }
}
