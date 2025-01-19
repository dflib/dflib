package org.dflib.fs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FSFolder_OfTest {

    static Path BASE_FOLDER;

    @BeforeAll
    static void initBaseFolder() throws URISyntaxException {
        BASE_FOLDER = Path.of(FSFolder_OfTest.class.getResource("test1").toURI());
    }

    @Test
    void path() {
        FSFolder f = FSFolder.of(BASE_FOLDER);
        assertEquals(List.of(BASE_FOLDER.resolve("test1.txt"), BASE_FOLDER.resolve("test2.md")), f.list());
    }

    @Test
    void file() {

        File file = BASE_FOLDER.toFile();
        FSFolder f = FSFolder.of(file);
        assertEquals(List.of(BASE_FOLDER.resolve("test1.txt"), BASE_FOLDER.resolve("test2.md")), f.list());
    }

    @Test
    void fileName() {
        String fileName = BASE_FOLDER.toFile().getPath();
        FSFolder f = FSFolder.of(fileName);
        assertEquals(List.of(BASE_FOLDER.resolve("test1.txt"), BASE_FOLDER.resolve("test2.md")), f.list());
    }
}
