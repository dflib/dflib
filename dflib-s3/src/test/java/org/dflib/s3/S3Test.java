package org.dflib.s3;

import org.dflib.DataFrame;
import org.dflib.csv.Csv;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class S3Test extends S3LocalTest {

    private S3Builder defaultBuilder() {
        return S3.builder()
                .client(client())
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
}
