package org.dflib.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S3_PathTest {

    @ParameterizedTest
    @ValueSource(strings = {"sub", "sub/"})
    void path(String subfolder) {
        S3 s3 = S3.of("s3://b/data");
        S3 sub = s3.path(subfolder);

        assertEquals("b", sub.getBucket());
        assertEquals("data/sub/", sub.getPrefix());

        // must not change the original
        assertEquals("data/", s3.getPrefix());
    }

    @Test
    void path_BucketRoot() {
        assertEquals("sub/", S3.of("s3://b").path("sub").getPrefix());
    }

    @Test
    void path_Chained() {
        assertEquals("data/a/b/", S3.of("s3://b/data").path("a").path("b").getPrefix());
        assertEquals("data/a/b/", S3.of("s3://b/data").path("a/b").getPrefix());
    }

    @Test
    void path_Empty() {
        S3 s3 = S3.of("s3://b/data");
        assertSame(s3, s3.path(""));
    }

    @Test
    void path_Absolute() {
        assertThrows(IllegalArgumentException.class, () -> S3.of("s3://b/data").path("/sub"));
    }
}
