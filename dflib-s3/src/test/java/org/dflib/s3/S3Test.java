package org.dflib.s3;

import org.dflib.ByteSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class S3Test extends S3LocalTest {

    private static final String ROOT = "test";
    private static final Function<ByteSource, String> PROCESSOR = source -> new String(source.asBytes(), StandardCharsets.UTF_8);

    @BeforeAll
    static void setUp() {
        putTestObject(ROOT + "/data1.txt", "test_data1");
        putTestObject(ROOT + "/data2.txt", "test_data2");
        putTestObject(ROOT + "/subdir", "subdir_data");
        putTestObject(ROOT + "/subdir/data3.txt", "test_data3");
        putTestObject(ROOT + "/subdir/nested/data4.txt", "test_data4");
    }

    @Test
    void source_content() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(ROOT + "/data1.txt")
                .build();

        String data = PROCESSOR.apply(connector.source());

        assertEquals("test_data1", data);
    }

    @Test
    void sources_content() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(ROOT)
                .build();

        Map<String, String> contents = connector.sources().process((key, source) -> PROCESSOR.apply(source));

        assertEquals(5, contents.size());
        assertEquals("test_data1", contents.get(ROOT + "/data1.txt"));
        assertEquals("test_data2", contents.get(ROOT + "/data2.txt"));
    }

    @Test
    void list_exactKeyMatch() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(ROOT + "/data1.txt")
                .build();

        List<S3Object> objects = connector.list();

        assertEquals(1, objects.size());
        assertEquals(ROOT + "/data1.txt", objects.get(0).key());
        assertEquals(10, objects.get(0).size());
    }

    @Test
    void list_prefixMatch() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(ROOT)
                .build();

        List<S3Object> objects = connector.list();

        assertEquals(5, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data1.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data2.txt")));
    }

    @Test
    void list_prefixMatch_withSlash() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(ROOT + "/")
                .build();

        List<S3Object> objects = connector.list();

        assertEquals(5, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data1.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/data2.txt")));
    }

    @Test
    void list_noMatch() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key("nonexistent")
                .build();

        List<S3Object> objects = connector.list();
        assertTrue(objects.isEmpty());
    }

    @Test
    void list_mixedMatch() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(ROOT + "/subdir")
                .build();

        List<S3Object> objects = connector.list();

        assertEquals(3, objects.size());
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir/data3.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir/nested/data4.txt")));
    }

    @Test
    void list_mixedMatch_withSlash() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(ROOT + "/subdir/")
                .build();

        List<S3Object> objects = connector.list();

        assertEquals(2, objects.size());
        assertTrue(objects.stream().noneMatch(o -> o.key().equals(ROOT + "/subdir")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir/data3.txt")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals(ROOT + "/subdir/nested/data4.txt")));
    }
}
