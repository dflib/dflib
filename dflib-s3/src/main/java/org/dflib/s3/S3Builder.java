package org.dflib.s3;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

/**
 * A builder for {@link S3} connector that provides a flexible way to configure
 * all S3 options including region, credentials, endpoint configuration, etc.
 *
 * @since 2.0.0
 */
public class S3Builder {

    private S3ClientBuilder clientBuilder;
    private S3Client preconfiguredClient;
    private String bucket;
    private String key;

    S3Builder() {
        this.clientBuilder = S3Client.builder();
    }

    /**
     * Sets the S3 bucket name.
     */
    public S3Builder bucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    /**
     * Sets the S3 object key.
     */
    public S3Builder key(String key) {
        this.key = key;
        return this;
    }

    /**
     * Sets a pre-configured {@link S3Client} instance.
     * Note: This will override any previous client configuration settings.
     */
    public S3Builder client(S3Client s3Client) {
        preconfiguredClient = s3Client;
        clientBuilder = null;
        return this;
    }

    /**
     * Sets the AWS region.
     * Note: This has no effect if a pre-configured client is set via {@link #client(S3Client)}.
     */
    public S3Builder region(Region region) {
        if (clientBuilder != null) {
            clientBuilder.region(region);
        }
        return this;
    }

    /**
     * Sets the AWS credentials using access key ID and secret access key.
     * Note: This has no effect if a pre-configured client is set via {@link #client(S3Client)}.
     */
    public S3Builder credentials(String accessKeyId, String secretAccessKey) {
        return credentials(AwsBasicCredentials.create(accessKeyId, secretAccessKey));
    }

    /**
     * Sets the AWS credentials.
     * Note: This has no effect if a pre-configured client is set via {@link #client(S3Client)}.
     */
    public S3Builder credentials(AwsCredentials credentials) {
        if (clientBuilder != null) {
            clientBuilder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        }
        return this;
    }

    /**
     * Sets the AWS credentials provider.
     * Note: This has no effect if a pre-configured client is set via {@link #client(S3Client)}.
     */
    public S3Builder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
        if (clientBuilder != null) {
            clientBuilder.credentialsProvider(credentialsProvider);
        }
        return this;
    }

    /**
     * Builds a new {@link S3} connector with the configured options.
     *
     * @throws IllegalStateException if bucket or key is not set
     */
    public S3 build() {
        if (bucket == null) {
            throw new IllegalStateException("Bucket must be set");
        }
        if (key == null) {
            throw new IllegalStateException("Key must be set");
        }

        S3Client client = preconfiguredClient != null
                ? preconfiguredClient
                : clientBuilder.build();

        return new S3(client, bucket, key);
    }
}
