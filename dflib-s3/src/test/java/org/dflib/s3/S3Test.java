package org.dflib.s3;

import org.dflib.ByteSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class S3Test extends S3LocalTest {

    private static final String TEST_BUCKET = "test-bucket";
    private static final String TEST_KEY = "test/data.csv";

    private static final String ROOT = "plain";
    private static final Function<ByteSource, String> PROCESSOR = source -> new String(source.asBytes(),
            StandardCharsets.UTF_8);

    @BeforeAll
    static void setUp() {
        putTestObject(ROOT + "/data1.txt", "test_data1");
        putTestObject(ROOT + "/data2.txt", "test_data2");
        putTestObject(ROOT + "/subdir", "subdir_data");
        putTestObject(ROOT + "/subdir/data3.txt", "test_data3");
        putTestObject(ROOT + "/subdir/nested/data4.txt", "test_data4");
    }

    @Test
    void builder_missingBucket() {
        assertThrows(NullPointerException.class, () -> S3.of(testClient(), null, TEST_KEY));
    }

    @Test
    void builder_missingKey() {
        assertThrows(NullPointerException.class, () -> S3.of(testClient(), TEST_BUCKET, null));
    }

    @Test
    void builder_withRegionAndCredentials() {
        S3 connector = S3.of(Region.US_EAST_1, DefaultCredentialsProvider.create(), "accessKey", "secretKey");
        assertNotNull(connector);
    }

    @Test
    void source_uri() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, TEST_KEY);
        assertEquals("s3://" + TEST_BUCKET + "/" + TEST_KEY, connector.source().uri().orElse(null));
    }

    @Test
    void resolve_append() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, "base").resolve("subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", connector.source().uri().orElse(null));
    }

    @Test
    void resolve_appendWithLeadingSlash() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, "base").resolve("/subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", connector.source().uri().orElse(null));
    }

    @Test
    void resolve_appendToKeyWithTrailingSlash() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, "base/").resolve("subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", connector.source().uri().orElse(null));
    }

    @Test
    void resolve_empty() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, "base/").resolve("");
        assertEquals("s3://" + TEST_BUCKET + "/base/", connector.source().uri().orElse(null));
    }

    @Test
    void resolve_null() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, "base").resolve(null);
        assertEquals("s3://" + TEST_BUCKET + "/base", connector.source().uri().orElse(null));
    }

    @Test
    void source_content() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT + "/data1.txt");

        String data = PROCESSOR.apply(connector.source());

        assertEquals("test_data1", data);
    }

    @Test
    void sources_content() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT);

        Map<String, String> contents = connector.sources().process((key, source) -> PROCESSOR.apply(source));

        assertEquals(3, contents.size());
        assertEquals("test_data1", contents.get(ROOT + "/data1.txt"));
        assertEquals("test_data2", contents.get(ROOT + "/data2.txt"));
    }

    @Test
    void list_exactKeyMatch() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT + "/data1.txt");

        List<S3Object> objects = connector.list();

        assertEquals(1, objects.size());
        assertEquals(ROOT + "/data1.txt", objects.get(0).key());
        assertEquals(10, objects.get(0).size());
    }

    @Test
    void list_prefixMatch() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT);

        List<S3Object> objects = connector.list();

        assertEquals(3, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data1.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data2.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir")));
        assertTrue(objects.stream().noneMatch(o -> o.key().contains("nested")));
    }

    @Test
    void list_prefixMatch_recursive() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT);

        List<S3Object> objects = connector.list(true);

        assertEquals(5, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data1.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data2.txt")));
    }

    @Test
    void list_prefixMatch_withSlash() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT + "/");

        List<S3Object> objects = connector.list();

        assertEquals(3, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data1.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data2.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir")));
        assertTrue(objects.stream().noneMatch(o -> o.key().contains("nested")));
    }

    @Test
    void list_prefixMatch_withSlash_recursive() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT + "/");

        List<S3Object> objects = connector.list(true);

        assertEquals(5, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data1.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data2.txt")));
    }

    @Test
    void list_noMatch() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, "nonexistent");

        List<S3Object> objects = connector.list();
        assertTrue(objects.isEmpty());
    }

    @Test
    void list_mixedMatch() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT + "/subdir");

        List<S3Object> objects = connector.list(true);

        assertEquals(3, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir/data3.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir/nested/data4.txt")));
    }

    @Test
    void list_mixedMatch_withSlash() {
        S3 connector = S3.of(testClient(), TEST_BUCKET, ROOT + "/subdir/");

        List<S3Object> objects = connector.list(true);

        assertEquals(2, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir/data3.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir/nested/data4.txt")));
        assertTrue(objects.stream().noneMatch(o -> o.key().equals(ROOT + "/subdir")));
    }
}
