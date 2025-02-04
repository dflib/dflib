package org.dflib.s3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * An S3 connector that provides access to objects in an S3 bucket.
 *
 * @since 2.0.0
 */
public class S3 {

    private final S3Client s3Client;
    private final String bucket;
    private final String key;

    S3(S3Client s3Client, String bucket, String key) {
        this.s3Client = Objects.requireNonNull(s3Client);
        this.bucket = Objects.requireNonNull(bucket);
        this.key = Objects.requireNonNull(key);
    }

    /**
     * Creates a new builder for configuring an S3 connector.
     */
    public static S3Builder builder() {
        return new S3Builder();
    }

    /**
     * Creates a new S3 connector by resolving a path relative to this connector's key.
     */
    public S3 resolve(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }

        String newKey = key;
        if (!newKey.endsWith("/")) {
            newKey += "/";
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return new S3(s3Client, bucket, newKey + path);
    }

    /**
     * Lists S3 objects matching this connector's key.
     * <p>
     * For keys not ending with "/", tries to find objects with exact key first,
     * then with key + "/" if none found. For keys ending with "/", lists all objects
     * under that prefix.
     *
     * @return a list of S3 objects with their metadata, empty list if no objects found
     */
    public List<S3Object> list() {
        List<S3Object> result = Collections.emptyList();
        if (!key.endsWith("/")) {
            result = listObjectsUnderPrefix(key);
        }
        if (result.isEmpty()) {
            result = listObjectsUnderPrefix(key.endsWith("/") ? key : key + "/");
        }
        return result;
    }

    /**
     * Creates a {@link ByteSource} to read data from S3.
     */
    public ByteSource source() {
        return source(key);
    }

    ByteSource source(String key) {
        return new S3ByteSource(s3Client, bucket, key);
    }

    /**
     * Creates a {@link ByteSources} to read data from S3.
     */
    public ByteSources sources() {
        return new S3ByteSources(this);
    }

    private List<S3Object> listObjectsUnderPrefix(String prefix) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();

        List<S3Object> results = new ArrayList<>();
        s3Client.listObjectsV2Paginator(request)
                .contents()
                .forEach(results::add);
        return results;
    }
}
