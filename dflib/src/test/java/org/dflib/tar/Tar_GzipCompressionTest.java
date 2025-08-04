package org.dflib.tar;

import org.dflib.ByteSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tar_GzipCompressionTest {

    @ParameterizedTest
    @ValueSource(strings = {"test1.tar.gz", "test1.tar.gzip"})
    public void sources_DecompressByExtension(String fileName) throws URISyntaxException {

        File tarGz = new File(getClass().getResource(fileName).toURI());

        // capturing both source names and contents
        Map<String, String> labeledTexts = Tar.of(tarGz)
                .sources()
                .process((n, s) -> s.uri().orElse("") + ":" + new String(s.asBytes()));

        // directories must be skipped
        assertEquals(Map.of(
                "test.txt", "test.txt:test file contents",
                "a/test3.txt", "a/test3.txt:test 3 file contents",
                "a/test2.txt", "a/test2.txt:test 2 file contents",
                "b/c/test4.txt", "b/c/test4.txt:test 4 file contents"), labeledTexts);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test1.tar.gz", "test1.tar.gzip"})
    public void source_DecompressByExtension(String fileName) throws URISyntaxException {

        File tarGz = new File(getClass().getResource(fileName).toURI());
        ByteSource test2 = Tar.of(tarGz)
                .source("a/test2.txt");

        assertEquals("test 2 file contents", new String(test2.asBytes()));
    }
}
