package org.dflib.s3;

import org.dflib.ByteSource;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Objects;

/**
 * A connector to work with Amazon S3 objects.
 * Provides a {@link ByteSource} implementation that can be used with various
 * DFLib loaders to read data in different formats.
 *
 * @since 2.0.0
 */
public class S3 {

    private final S3Client s3Client;
    private final String bucket;
    private final String key;

    public static S3 of(String bucket, String key) {
        return new S3(S3Client.builder().build(), bucket, key);
    }

    public static S3 of(S3Client s3Client, String bucket, String key) {
        return new S3(s3Client, bucket, key);
    }

    private S3(S3Client s3Client, String bucket, String key) {
        this.s3Client = Objects.requireNonNull(s3Client);
        this.bucket = Objects.requireNonNull(bucket);
        this.key = Objects.requireNonNull(key);
    }

    /**
     * Returns a new connector with the provided path appended to this connector's key.
     */
    public S3 path(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }

        String newKey = key;
        if (!newKey.endsWith("/")) {
            newKey = newKey + "/";
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return new S3(s3Client, bucket, newKey + path);
    }

    /**
     * Creates a {@link ByteSource} to read data from S3.
     */
    public ByteSource source() {
        return new S3ByteSource(s3Client, bucket, key);
    }
}
