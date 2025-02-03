package org.dflib.fs;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FSFolder_SourceTest {

    @Test
    void inFolder() throws URISyntaxException {
        FSFolder f = FSFolder
                .of(Path.of(getClass().getResource("test1").toURI()));

        assertEquals("test1 text", new String(f.source("test1.txt").asBytes()));
    }

    @Test
    void inSubfolder() throws URISyntaxException {
        FSFolder f = FSFolder
                .of(Path.of(getClass().getResource("test1").toURI()))
                .includeSubfolders();

        assertEquals("subtest1 text", new String(f.source(Path.of("subtest1", "subtest1.txt")).asBytes()));
    }

    @Test
    void inSubfolder_SubfoldersDisallowedButStillAccessibleByPath() throws URISyntaxException {
        FSFolder f = FSFolder
                .of(Path.of(getClass().getResource("test1").toURI()));

        assertEquals("subtest1 text", new String(f.source(Path.of("subtest1", "subtest1.txt")).asBytes()));
    }

    @Test
    void extFilteredOutButStillAccessibleByPath() throws URISyntaxException {
        FSFolder f = FSFolder
                .of(Path.of(getClass().getResource("test1").toURI()))
                .includeExtension("md");

        assertEquals("test1 text", new String(f.source("test1.txt").asBytes()));
    }
}
