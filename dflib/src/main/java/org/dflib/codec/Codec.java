package org.dflib.codec;

import org.dflib.ByteSource;

import java.io.OutputStream;
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

        // null may be encountered when savers are sendin output to an in-memory stream
        if (uri == null) {
            return Optional.empty();
        }

        int dot = uri.lastIndexOf('.');
        if (dot < 0 || dot == uri.length() - 1) {
            return Optional.empty();
        }

        String extension = uri.substring(dot + 1);
        return switch (extension) {
            case "gz", "gzip" -> Optional.of(GZIP);
            default -> Optional.empty();
        };
    }

    ByteSource decompress(ByteSource compressed);

    /**
     * @deprecated a placeholder until we implement ByteTarget analog of ByteSource
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    OutputStream compress(OutputStream uncompressed);
}
