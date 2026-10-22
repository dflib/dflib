package org.dflib.s3;

import org.dflib.codec.Codec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S3_SourceTest {

    @Test
    void uri() {
        assertEquals("s3://b/data/x/y.csv", S3.of("s3://b/data").source("x/y.csv").uri().get());
        assertEquals("s3://b/y.csv", S3.of("s3://b").source("y.csv").uri().get());
    }

    @Test
    void uri_Compression() {
        String uri = S3.of("s3://b/data").source("a.csv.gz").uri().get();
        assertEquals(Codec.GZIP, Codec.ofUri(uri).get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/a.csv", "dir/"})
    void source_Invalid(String key) {
        assertThrows(IllegalArgumentException.class, () -> S3.of("s3://b/data").source(key));
    }
}
