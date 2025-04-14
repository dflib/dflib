package org.dflib.s3;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

public class S3LocalTest {

    protected static final String TEST_BUCKET = "test-bucket";

    private static final LocalStackContainer LOCAL_STACK;
    private static S3Client s3Client;

    static {
        LOCAL_STACK = new LocalStackContainer(DockerImageName.parse("localstack/localstack:4"))
                .withServices(LocalStackContainer.Service.S3);
        LOCAL_STACK.start();
    }

    @BeforeAll
    static void setUp() {
        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(LOCAL_STACK.getAccessKey(), LOCAL_STACK.getSecretKey())))
                .region(Region.of(LOCAL_STACK.getRegion()))
                .endpointOverride(LOCAL_STACK.getEndpointOverride(LocalStackContainer.Service.S3))
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();

        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(TEST_BUCKET)
                .build());
    }

    protected static S3Client testClient() {
        return s3Client;
    }

    protected static void putTestObject(String key, String content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(TEST_BUCKET)
                .key(key)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
    }

    protected static void putTestObject(String key, File file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(TEST_BUCKET)
                .key(key)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    }
}
