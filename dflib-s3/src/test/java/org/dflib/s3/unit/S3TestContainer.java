package org.dflib.s3.unit;

import org.dflib.f.CachingSupplier;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;

/**
 * Starts an S3-compatible storage (Adobe S3Mock) in Docker once per JVM and populates a test bucket.
 */
public class S3TestContainer {

    public static final String BUCKET = "dflib-test";

    private static final String IMAGE = "adobe/s3mock:5.2.3";
    private static final int PORT = 9090;

    private static final Supplier<S3Client> CLIENT = new CachingSupplier<>(S3TestContainer::start);

    public static boolean dockerAvailable() {
        return DockerClientFactory.instance().isDockerAvailable();
    }

    public static S3Client client() {
        return CLIENT.get();
    }

    private static S3Client start() {
        GenericContainer<?> container = new GenericContainer<>(IMAGE)
                .withExposedPorts(PORT)
                .withEnv("COM_ADOBE_TESTING_S3MOCK_STORE_INITIAL_BUCKETS", BUCKET);

        // we are working in a GLOBAL scope, so just start the container as early as we can, and don't worry about
        // shutdown...
        container.start();

        S3Client client = S3Client.builder()
                .endpointOverride(URI.create("http://" + container.getHost() + ":" + container.getMappedPort(PORT)))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                // third-party S3 implementations require path-style access and don't support the newer checksums
                .forcePathStyle(true)
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();

        populate(client);
        return client;
    }

    private static void populate(S3Client client) {
        put(client, "root.csv", "A,B\n0,r\n");
        put(client, "data.csv", "A,B\n0,d\n");
        put(client, "data_old/x.csv", "A,B\n0,o\n");

        put(client, "data/", "");
        put(client, "data/a.csv", "A,B\n1,x\n2,y\n");
        put(client, "data/b.csv.gz", gzip("A,B\n3,z\n"));
        put(client, "data/notes.txt", "notes");
        put(client, "data/.hidden.csv", "A,B\n5,h\n");
        put(client, "data/.git/config", "config");
        put(client, "data/sub/c.csv", "A,B\n4,w\n");
        put(client, "data/empty/", "");

        for (int i = 1; i <= 5; i++) {
            put(client, "paging/f" + i + ".txt", "f" + i);
        }

        for (int i = 1; i <= 3; i++) {
            put(client, "paging/d" + i + "/x.txt", "d" + i);
        }
    }

    private static void put(S3Client client, String key, String content) {
        put(client, key, content.getBytes(StandardCharsets.UTF_8));
    }

    private static void put(S3Client client, String key, byte[] content) {
        client.putObject(b -> b.bucket(BUCKET).key(key), RequestBody.fromBytes(content));
    }

    private static byte[] gzip(String content) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzOut = new GZIPOutputStream(out)) {
            gzOut.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return out.toByteArray();
    }
}
