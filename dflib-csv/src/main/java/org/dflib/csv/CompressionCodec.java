package org.dflib.csv;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public enum CompressionCodec {
    GZIP, ZIP;

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
            case "zip":
                return ZIP;
            default:
                return null;
        }
    }
}
