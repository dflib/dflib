package org.dflib.s3;

import org.dflib.s3.unit.BaseS3Test;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class S3_SourcesIT extends BaseS3Test {

    @Test
    void sources_IncludeExtension() {
        Map<String, String> content = s3("data")
                .includeExtension("txt")
                .sources()
                .process((n, s) -> new String(s.asBytes()));

        assertEquals(Map.of("notes.txt", "notes"), content);
    }

    @Test
    void sources_IncludeSubfolders() {
        Map<String, String> content = s3("data")
                .includeSubfolders()
                .includeExtension("csv")
                .sources()
                .process((n, s) -> new String(s.asBytes()));

        assertEquals(Map.of("a.csv", "A,B\n1,x\n2,y\n", "sub/c.csv", "A,B\n4,w\n"), content);
    }

    @Test
    void sources_Path() {
        Map<String, String> content = s3("data")
                .path("sub")
                .sources()
                .process((n, s) -> new String(s.asBytes()));

        assertEquals(Map.of("c.csv", "A,B\n4,w\n"), content);
    }

    @Test
    void sources_NoSuchFolder() {
        Map<String, String> content = s3("no-such-folder")
                .sources()
                .process((n, s) -> new String(s.asBytes()));

        assertEquals(Map.of(), content);
    }
}
