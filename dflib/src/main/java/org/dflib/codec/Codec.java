package org.dflib.codec;

import org.dflib.ByteSource;

import java.util.Objects;
import java.util.Optional;

/**
 * Supported codecs. Used for ByteSource decompression, etc. Defined as a set of String constants instead of an enum
 * to allow users to specify their own compression algorithms.
 *
 * @since 2.0.0
 */
public interface Codec {

    Codec GZIP = new GzipCodec();

    static Optional<Codec> ofUri(String uri) {
        Objects.requireNonNull(uri);

        int dot = uri.lastIndexOf('.');
        if (dot < 0 || dot == uri.length() - 1) {
            return null;
        }

        String extension = uri.substring(dot + 1);
        switch (extension) {
            case "gz":
            case "gzip":
                return Optional.of(GZIP);
            default:
                return Optional.empty();
        }
    }

    ByteSource decompress(ByteSource compressed);
}
