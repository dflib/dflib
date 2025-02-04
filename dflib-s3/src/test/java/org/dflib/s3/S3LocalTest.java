package org.dflib.s3;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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

@Testcontainers
public class S3LocalTest {

    protected static final String TEST_BUCKET = "test-bucket";
    protected static final String TEST_KEY = "test/data.csv";
    protected static final String TEST_DATA = "col1,col2\nvalue1,value2\nvalue3,value4";

    @Container
    private static final LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:2.3"))
            .withServices(LocalStackContainer.Service.S3);

    private static S3Client s3Client;

    @BeforeAll
    static void setupS3Client() {
        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
                .region(Region.of(localStack.getRegion()))
                .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();

        s3Client.createBucket(CreateBucketRequest.builder().bucket(TEST_BUCKET).build());
        s3Client.putObject(
                PutObjectRequest.builder().bucket(TEST_BUCKET).key(TEST_KEY).build(),
                RequestBody.fromString(TEST_DATA));
    }

    protected S3Client client() {
        return s3Client;
    }
}
