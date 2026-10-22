package org.dflib.s3;

import org.dflib.ByteSource;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @since 2.0.0
 */
class S3ByteSource implements ByteSource {

    private final Supplier<S3Client> client;
    private final String bucket;
    private final String key;

    S3ByteSource(Supplier<S3Client> client, String bucket, String key) {
        this.client = client;
        this.bucket = bucket;
        this.key = key;
    }

    @Override
    public Optional<String> uri() {
        return Optional.of("s3://" + bucket + "/" + key);
    }

    @Override
    public InputStream stream() {

        // the client may be initialized lazily, so resolve it at the very last moment
        S3Client client = this.client.get();
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();

        try {
            return client.getObject(request);
        } catch (SdkException e) {
            throw new RuntimeException("Error reading S3 object: " + uri().get(), e);
        }
    }
}
