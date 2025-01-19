package org.dflib.fs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FSFolder_ListTest {

    @Test
    void noSubfolders_includeFolders() throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p);
        assertEquals(List.of(
                p.resolve(""), // self
                p.resolve("subtest1"),
                p.resolve("subtest2"),
                p.resolve("test1.txt"),
                p.resolve("test2.md")), f.list(true));
    }

    @Test
    void noSubfolders() throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p);
        assertEquals(List.of(p.resolve("test1.txt"), p.resolve("test2.md")), f.list());
    }

    @Test
    void hidden() throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p).includeHidden();
        assertEquals(List.of(p.resolve(".test_hidden.txt"), p.resolve("test1.txt"), p.resolve("test2.md")), f.list());
    }

    @ParameterizedTest
    @ValueSource(strings = {"md", ".md"})
    void noSubfolders_ext(String ext) throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p).includeExtension(ext);
        assertEquals(List.of(p.resolve("test2.md")), f.list());
    }

    @Test
    void subfolders() throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p).includeSubfolders();
        assertEquals(List.of(
                p.resolve(Path.of("subtest1", "subtest1.txt")),
                p.resolve(Path.of("subtest2", "subsubtest2", "subsubtest2.md")),
                p.resolve(Path.of("subtest2", "subsubtest2", "subsubtest2.txt")),
                p.resolve("test1.txt"),
                p.resolve("test2.md")
        ), f.list());
    }

    @ParameterizedTest
    @ValueSource(strings = {"md", ".md"})
    void subfolders_ext(String ext) throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p).includeSubfolders().includeExtension(ext);
        assertEquals(List.of(
                p.resolve(Path.of("subtest2", "subsubtest2", "subsubtest2.md")),
                p.resolve("test2.md")
        ), f.list());
    }
}
