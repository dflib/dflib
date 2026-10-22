package org.dflib.s3;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S3_OfTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "s3://b|b|''",
            "s3://b/|b|''",
            "s3://b/data|b|data/",
            "s3://b/data/|b|data/",
            "s3://b/data/2024|b|data/2024/",
            "b|b|''",
            "b/data|b|data/",
            "b/data/|b|data/"
    })
    void of(String location, String expectedBucket, String expectedPrefix) {
        S3 s3 = S3.of(location);
        assertEquals(expectedBucket, s3.getBucket());
        assertEquals(expectedPrefix, s3.getPrefix());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "s3://", "s3:///k", "/k", "http://b/k", "s3a://b/k"})
    void of_Invalid(String location) {
        assertThrows(IllegalArgumentException.class, () -> S3.of(location));
    }

    @Test
    void of_Null() {
        assertThrows(NullPointerException.class, () -> S3.of(null));
    }

    @Test
    void of_ToString() {
        assertEquals("s3://b/", S3.of("b").toString());
        assertEquals("s3://b/data/", S3.of("s3://b/data").toString());
    }
}
