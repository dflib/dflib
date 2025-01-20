package org.dflib.fs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FSFolder_SourcesTest {

    @Test
    void noSubfolders() throws URISyntaxException {
        FSFolder f = FSFolder.of(Path.of(getClass().getResource("test1").toURI()));

        Map<String, String> content = f.sources().process((n, s) -> new String(s.asBytes()));
        assertEquals(2, content.size());
        assertEquals("test1 text", content.get("test1.txt"));
        assertEquals("# test2 header", content.get("test2.md"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"md", ".md"})
    void noSubfolders_ext(String ext) throws URISyntaxException {
        FSFolder f = FSFolder
                .of(Path.of(getClass().getResource("test1").toURI()))
                .includeExtension(ext);

        Map<String, String> content = f.sources().process((n, s) -> new String(s.asBytes()));

        assertEquals(1, content.size());
        assertEquals("# test2 header", content.get("test2.md"));
    }

    @Test
    void subfolders() throws URISyntaxException {
        FSFolder f = FSFolder
                .of(Path.of(getClass().getResource("test1").toURI()))
                .includeSubfolders();

        Map<String, String> content = f.sources().process((n, s) -> new String(s.asBytes()));

        assertEquals(5, content.size());
        assertEquals("test1 text", content.get("test1.txt"));
        assertEquals("# test2 header", content.get("test2.md"));
        assertEquals("subtest1 text", content.get("subtest1/subtest1.txt"));
        assertEquals("subsubtest2 text", content.get("subtest2/subsubtest2/subsubtest2.txt"));
        assertEquals("# subsubtest2 header", content.get("subtest2/subsubtest2/subsubtest2.md"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"md", ".md"})
    void subfolders_ext(String ext) throws URISyntaxException {
        FSFolder f = FSFolder
                .of(Path.of(getClass().getResource("test1").toURI()))
                .includeSubfolders()
                .includeExtension(ext);

        Map<String, String> content = f.sources().process((n, s) -> new String(s.asBytes()));

        assertEquals(2, content.size());
        assertEquals("# test2 header", content.get("test2.md"));
        assertEquals("# subsubtest2 header", content.get("subtest2/subsubtest2/subsubtest2.md"));
    }
}
