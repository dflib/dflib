package org.dflib.fs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FSFolder_ListTest {

    @Test
    void noSubfolders() throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p);
        assertEquals(Set.of(p.resolve("test1.txt"), p.resolve("test2.md")), Set.copyOf(f.list()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"md", ".md"})
    void noSubfolders_ext(String ext) throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p).includeExtension(ext);
        assertEquals(Set.of(p.resolve("test2.md")), Set.copyOf(f.list()));
    }

    @Test
    void subfolders() throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p).includeSubfolders();
        assertEquals(Set.of(
                p.resolve("test1.txt"),
                p.resolve("test2.md"),
                p.resolve(Path.of("subtest1", "subtest1.txt")),
                p.resolve(Path.of("subtest2", "subsubtest2", "subsubtest2.txt")),
                p.resolve(Path.of("subtest2", "subsubtest2", "subsubtest2.md"))
        ), Set.copyOf(f.list()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"md", ".md"})
    void subfolders_ext(String ext) throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test1").toURI());
        FSFolder f = FSFolder.of(p).includeSubfolders().includeExtension(ext);
        assertEquals(Set.of(
                p.resolve("test2.md"),
                p.resolve(Path.of("subtest2", "subsubtest2", "subsubtest2.md"))
        ), Set.copyOf(f.list()));
    }
}
