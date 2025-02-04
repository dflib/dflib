package org.dflib.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static org.junit.jupiter.api.Assertions.*;

class S3BuilderTest {

    private static final String TEST_BUCKET = "test-bucket";
    private static final String TEST_KEY = "test/data.csv";
    private S3Client defaultS3Client;

    @BeforeEach
    void setUp() {
        defaultS3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    @Test
    void builder_missingBucket() {
        var builder = S3.builder()
                .client(defaultS3Client)
                .key(TEST_KEY);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void builder_missingKey() {
        var builder = S3.builder()
                .client(defaultS3Client)
                .bucket(TEST_BUCKET);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void builder_withRegion() {
        S3 connector = S3.builder()
                .bucket(TEST_BUCKET)
                .key(TEST_KEY)
                .region(Region.US_EAST_1)
                .build();

        assertNotNull(connector);
    }

    @Test
    void builder_withBasicCredentials() {
        S3 connector = S3.builder()
                .bucket(TEST_BUCKET)
                .key(TEST_KEY)
                .region(Region.US_EAST_1)
                .credentials("accessKey", "secretKey")
                .build();

        assertNotNull(connector);
    }

    @Test
    void builder_withCredentials() {
        AwsSessionCredentials sessionCredentials = AwsSessionCredentials.create(
                "accessKey",
                "secretKey",
                "sessionToken");

        S3 connector = S3.builder()
                .bucket(TEST_BUCKET)
                .key(TEST_KEY)
                .region(Region.US_EAST_1)
                .credentials(sessionCredentials)
                .build();

        assertNotNull(connector);
    }

    @Test
    void builder_withCredentialsProvider() {
        S3 connector = S3.builder()
                .bucket(TEST_BUCKET)
                .key(TEST_KEY)
                .region(Region.US_EAST_1)
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .build();

        assertNotNull(connector);
    }

    @Test
    void source_uri() {
        S3 connector = S3.builder()
                .client(defaultS3Client)
                .bucket(TEST_BUCKET)
                .key(TEST_KEY)
                .build();

        assertEquals("s3://" + TEST_BUCKET + "/" + TEST_KEY, connector.source().uri().orElse(null));
    }

    @Test
    void path_append() {
        S3 connector = S3.builder()
                .client(defaultS3Client)
                .bucket(TEST_BUCKET)
                .key("base")
                .build();

        S3 withPath = connector.path("subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", withPath.source().uri().orElse(null));
    }

    @Test
    void path_appendWithLeadingSlash() {
        S3 connector = S3.builder()
                .client(defaultS3Client)
                .bucket(TEST_BUCKET)
                .key("base")
                .build();

        S3 withPath = connector.path("/subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", withPath.source().uri().orElse(null));
    }

    @Test
    void path_appendToKeyWithTrailingSlash() {
        S3 connector = S3.builder()
                .client(defaultS3Client)
                .bucket(TEST_BUCKET)
                .key("base/")
                .build();

        S3 withPath = connector.path("subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", withPath.source().uri().orElse(null));
    }

    @Test
    void path_empty() {
        S3 connector = S3.builder()
                .client(defaultS3Client)
                .bucket(TEST_BUCKET)
                .key("base")
                .build();

        S3 withPath = connector.path("");
        assertEquals("s3://" + TEST_BUCKET + "/base", withPath.source().uri().orElse(null));
    }

    @Test
    void path_null() {
        S3 connector = S3.builder()
                .client(defaultS3Client)
                .bucket(TEST_BUCKET)
                .key("base")
                .build();

        S3 withPath = connector.path(null);
        assertEquals("s3://" + TEST_BUCKET + "/base", withPath.source().uri().orElse(null));
    }
}
