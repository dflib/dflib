package org.dflib.s3;

import org.dflib.f.CachingSupplier;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Supplier;

/**
 * Holds a shared S3Client that is created lazily on first access and is never closed.
 *
 * @since 2.0.0
 */
class S3DefaultClient {

    private static final Supplier<S3Client> CLIENT = new CachingSupplier<>(S3DefaultClient::create);

    static Supplier<S3Client> supplier() {
        return CLIENT;
    }

    private static S3Client create() {
        try {
            return S3Client.builder()
                    // "s3://" locations carry no region info, so allow accessing buckets in any region
                    .crossRegionAccessEnabled(true)
                    .build();
        } catch (SdkException e) {
            throw new RuntimeException("Failed to create a default S3Client. Configure AWS region and credentials " +
                    "(e.g., via environment variables or '~/.aws/config'), or pass an explicit client via " +
                    "'S3.client(..)'", e);
        }
    }
}
