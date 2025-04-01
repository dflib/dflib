package org.dflib.s3;

import org.dflib.ByteSource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.Optional;

/**
 * A {@link ByteSource} implementation that reads data from Amazon S3.
 *
 * @since 2.0.0
 */
class S3ByteSource implements ByteSource {

    private final S3Client s3Client;
    private final String bucket;
    private final String key;

    public S3ByteSource(S3Client s3Client, String bucket, String key) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.key = key;
    }

    @Override
    public Optional<String> uri() {
        return Optional.of("s3://" + bucket + "/" + key);
    }

    @Override
    public InputStream stream() {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try {
            return s3Client.getObject(request);
        } catch (Exception e) {
            throw new RuntimeException("Error reading S3 object: s3://" + bucket + "/" + key, e);
        }
    }
}
