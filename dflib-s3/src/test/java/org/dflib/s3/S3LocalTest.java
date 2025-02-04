package org.dflib.s3;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class S3LocalTest {

    protected static final String TEST_BUCKET = "test-bucket";
    protected static final String TEST_KEY = "test/data.csv";
    protected static final String TEST_DATA = "col1,col2\nvalue1,value2\nvalue3,value4";

    @Container
    private static final LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:4"))
            .withServices(LocalStackContainer.Service.S3);

    private static S3Client s3Client;

    protected S3Client testClient() {
        return s3Client;
    }

    @BeforeAll
    static void setUp() {
        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
                .region(Region.of(localStack.getRegion()))
                .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();

        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(TEST_BUCKET)
                .build());

        putData();
    }

    private static void putData() {
        putTestObject(TEST_KEY, TEST_DATA);
        createListTestData();
    }

    private static void createListTestData() {
        putTestObject("test/data1.csv", "data1");
        putTestObject("test/data2.csv", "data2");
        putTestObject("test/subdir/data1.csv", "nested-data1");
        putTestObject("test/subdir/nested/data2.csv", "nested-data2");
        putTestObject("test/subdir", "file-named-subdir");
        putTestObject("test/subdir/more/data3.csv", "more-data");
    }

    private static void putTestObject(String key, String content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(TEST_BUCKET)
                .key(key)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
    }
}
