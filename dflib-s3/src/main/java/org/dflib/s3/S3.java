package org.dflib.s3;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An S3 connector that provides access to objects in an S3 bucket.
 *
 * @since 2.0.0
 */
public class S3 {

    private final S3Client s3Client;
    private final String bucket;
    private final String key;

    private S3(S3Client s3Client, String bucket, String key) {
        this.s3Client = Objects.requireNonNull(s3Client);
        this.bucket = Objects.requireNonNull(bucket);
        this.key = Objects.requireNonNull(key);
    }

    /**
     * Creates a new S3 connector with the default S3 client.
     * <p>
     * The default client is created using {@link S3Client#create()}, which uses the default configuration chain.
     */
    public static S3 of(String bucket, String key) {
        return new S3(S3Client.create(), bucket, key);
    }

    /**
     * Creates a new S3 connector with the specified region and credentials.
     */
    public static S3 of(Region region, AwsCredentialsProvider credentialsProvider, String bucket, String key) {
        S3Client client = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
        return new S3(client, bucket, key);
    }

    /**
     * Creates a new S3 connector with the specified S3 client.
     */
    public static S3 of(S3Client s3Client, String bucket, String key) {
        return new S3(s3Client, bucket, key);
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
     * For keys not ending with "/", tries to find objects with exact key first, then with key + "/" if none found.
     * For keys ending with "/", lists all objects under that prefix.
     *
     * @return a list of S3 objects with their metadata, empty list if no objects found
     */
    public List<S3Object> list() {
        return list(false);
    }

    /**
     * Lists S3 objects matching this connector's key.
     * <p>
     * For keys not ending with "/", tries to find objects with exact key first, then with key + "/" if none found.
     * For keys ending with "/", lists all objects under that prefix.
     *
     * @param recursive if true, lists objects in subdirectories recursively.
     *                  If false, only lists objects in the current directory.
     * @return a list of S3 objects with their metadata, empty list if no objects found
     */
    public List<S3Object> list(boolean recursive) {
        List<S3Object> result = Collections.emptyList();
        if (!key.endsWith("/")) {
            result = listObjectsUnderPrefix(key, recursive);
        }
        if (result.isEmpty()) {
            result = listObjectsUnderPrefix(key.endsWith("/") ? key : key + "/", recursive);
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

    private List<S3Object> listObjectsUnderPrefix(String prefix, boolean recursive) {
        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix);

        if (!recursive) {
            requestBuilder.delimiter("/");
        }

        List<S3Object> results = new ArrayList<>();
        s3Client.listObjectsV2Paginator(requestBuilder.build())
                .contents()
                .forEach(results::add);
        return results;
    }
}
