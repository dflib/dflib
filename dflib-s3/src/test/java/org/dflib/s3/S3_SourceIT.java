package org.dflib.s3;

import org.dflib.ByteSource;
import org.dflib.s3.unit.BaseS3Test;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class S3_SourceIT extends BaseS3Test {

    @Test
    void source() {
        assertEquals("notes", new String(s3("data").source("notes.txt").asBytes()));
        assertEquals("A,B\n4,w\n", new String(s3("data").source("sub/c.csv").asBytes()));
    }

    @Test
    void source_Hidden() {
        assertEquals("A,B\n5,h\n", new String(s3("data").source(".hidden.csv").asBytes()));
    }

    @Test
    void source_NoSuchKey() {

        // must not fail until the source is read
        ByteSource src = s3("data").source("no-such-key.csv");

        RuntimeException e = assertThrows(RuntimeException.class, src::asBytes);
        assertTrue(e.getMessage().contains("s3://dflib-test/data/no-such-key.csv"), e.getMessage());
        assertInstanceOf(NoSuchKeyException.class, e.getCause());
    }
}
