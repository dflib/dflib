package org.dflib.s3;

import org.dflib.DataFrame;
import org.dflib.csv.Csv;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S3Test extends S3LocalTest {

    private S3Builder defaultBuilder() {
        return S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key(TEST_KEY);
    }

    @Test
    void source_content() {
        S3 connector = defaultBuilder().build();
        byte[] data = connector.source().asBytes();
        assertEquals(TEST_DATA, new String(data, StandardCharsets.UTF_8));
    }

    @Test
    void source_withCsvLoader() {
        S3 connector = defaultBuilder().build();
        DataFrame df = Csv.load(connector.source());

        assertNotNull(df);
        assertEquals(2, df.height());
        assertEquals(2, df.width());
        assertEquals("value1", df.getColumn("col1").get(0));
        assertEquals("value2", df.getColumn("col2").get(0));
        assertEquals("value3", df.getColumn("col1").get(1));
        assertEquals("value4", df.getColumn("col2").get(1));
    }

    @Test
    void sources_content() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key("test")
                .build();

        Map<String, String> contents = connector.sources()
                .process((key, source) -> new String(source.asBytes(), StandardCharsets.UTF_8));

        assertTrue(contents.size() >= 2);
        assertEquals("data1", contents.get("test/data1.csv"));
        assertEquals("data2", contents.get("test/data2.csv"));
    }

    @Test
    void sources_withCsvLoader() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key("test")
                .build();

        Map<String, DataFrame> dataFrames = connector.sources()
                .process((key, source) -> {
                    try {
                        return Csv.load(source);
                    } catch (Exception e) {
                        return null;
                    }
                });

        assertTrue(dataFrames.size() >= 2);
        assertNotNull(dataFrames.get("test/data1.csv"));
        assertNotNull(dataFrames.get("test/data2.csv"));
    }

    @Test
    void list_exactKeyMatch() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key("test/data1.csv")
                .build();

        List<S3Object> objects = connector.list();

        assertEquals(1, objects.size());
        assertEquals("test/data1.csv", objects.get(0).key());
        assertEquals(5, objects.get(0).size());
    }

    @Test
    void list_prefixMatch() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key("test")
                .build();

        List<S3Object> objects = connector.list();

        assertTrue(objects.size() >= 2);
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/data1.csv")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/data2.csv")));
    }

    @Test
    void list_prefixMatch_withSlash() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key("test/")
                .build();

        List<S3Object> objects = connector.list();

        assertTrue(objects.size() >= 2);
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/data1.csv")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/data2.csv")));
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
                .key("test/subdir")
                .build();

        List<S3Object> objects = connector.list();

        assertTrue(objects.size() >= 3);
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/subdir")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/subdir/data1.csv")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/subdir/nested/data2.csv")));
    }

    @Test
    void list_mixedMatch_withSlash() {
        S3 connector = S3.builder()
                .client(testClient())
                .bucket(TEST_BUCKET)
                .key("test/subdir/")
                .build();

        List<S3Object> objects = connector.list();

        assertTrue(objects.size() >= 2);
        assertTrue(objects.stream().noneMatch(o -> o.key().equals("test/subdir")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/subdir/data1.csv")));
        assertTrue(objects.stream().anyMatch(o -> o.key().equals("test/subdir/nested/data2.csv")));
    }
}
