package org.dflib.s3;

import org.dflib.DataFrame;
import org.dflib.csv.Csv;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
public class S3Test extends S3BaseTest {

    @Test
    void source_uri() {
        S3 connector = S3.of(client(), TEST_BUCKET, TEST_KEY);
        assertEquals("s3://" + TEST_BUCKET + "/" + TEST_KEY, 
                connector.source().uri().orElse(null));
    }

    @Test
    void source_content() {
        S3 connector = S3.of(client(), TEST_BUCKET, TEST_KEY);
        byte[] data = connector.source().asBytes();
        assertEquals(TEST_DATA, new String(data, StandardCharsets.UTF_8));
    }

    @Test
    void source_withCsvLoader() {
        S3 connector = S3.of(client(), TEST_BUCKET, TEST_KEY);
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
    void path_append() {
        S3 connector = S3.of(client(), TEST_BUCKET, "base");
        S3 withPath = connector.path("subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", withPath.source().uri().orElse(null));
    }

    @Test
    void path_appendWithLeadingSlash() {
        S3 connector = S3.of(client(), TEST_BUCKET, "base");
        S3 withPath = connector.path("/subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", withPath.source().uri().orElse(null));
    }

    @Test
    void path_appendToKeyWithTrailingSlash() {
        S3 connector = S3.of(client(), TEST_BUCKET, "base/");
        S3 withPath = connector.path("subdir/file.csv");
        assertEquals("s3://" + TEST_BUCKET + "/base/subdir/file.csv", withPath.source().uri().orElse(null));
    }

    @Test
    void path_empty() {
        S3 connector = S3.of(client(), TEST_BUCKET, "base");
        S3 withPath = connector.path("");
        assertEquals("s3://" + TEST_BUCKET + "/base", withPath.source().uri().orElse(null));
    }

    @Test
    void path_null() {
        S3 connector = S3.of(client(), TEST_BUCKET, "base");
        S3 withPath = connector.path(null);
        assertEquals("s3://" + TEST_BUCKET + "/base", withPath.source().uri().orElse(null));
    }
}
