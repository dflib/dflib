package org.dflib.csv;

import java.util.Objects;

/**
 * A list of supported compression codecs. For now only supports GZIP, but we'll be adding others later.
 *
 * @since 2.0.0
 */
public enum CompressionCodec {
    GZIP;

    public static CompressionCodec ofFileName(String fileName) {
        Objects.requireNonNull(fileName);

        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return null;
        }

        String extension = fileName.substring(dot + 1);
        switch (extension) {
            case "gz":
            case "gzip":
                return GZIP;
            default:
                return null;
        }
    }
}
