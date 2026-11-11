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

    private final Supplier<S3Client> clientSupplier;
    private final String bucket;
    private final String key;

    S3ByteSource(Supplier<S3Client> clientSupplier, String bucket, String key) {
        this.clientSupplier = clientSupplier;
        this.bucket = bucket;
        this.key = key;
    }

    @Override
    public Optional<String> uri() {
        return Optional.of(S3.SCHEME + bucket + "/" + key);
    }

    @Override
    public InputStream stream() {
        S3Client client = clientSupplier.get();
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();

        try {
            return client.getObject(request);
        } catch (SdkException e) {
            throw new RuntimeException("Error reading S3 object: " + uri().get(), e);
        }
    }
}
