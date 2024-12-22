package org.dflib.zip;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;

public class Zip_RandomAccessTest {

    @Test
    void ofFile() throws URISyntaxException {
        File zipFile = new File(getClass().getResource("test.zip").toURI());
        assertTrue(zipFile.exists());

        Zip zip = Zip.of(zipFile);
        assertTrue(zip instanceof Zip.RandomAccessZip);
    }

    @Test
    void ofFileName() throws URISyntaxException {
        File zipFile = new File(getClass().getResource("test.zip").toURI());
        Zip zip = Zip.of(zipFile.getAbsolutePath());
        assertTrue(zip instanceof Zip.RandomAccessZip);
    }

    @Test
    void ofPath() throws URISyntaxException {
        Zip zip = Zip.of(Path.of(getClass().getResource("test.zip").toURI()));
        assertTrue(zip instanceof Zip.RandomAccessZip);
    }

    @Test
    void list() throws URISyntaxException {
        Zip zip = Zip.of(Path.of(getClass().getResource("test.zip").toURI()));
        List<String> names = zip.list().stream().map(ZipEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("a/", "a/test2.txt", "a/test3.txt", "b/", "b/c/", "b/c/test4.txt", "test.txt"), names);
    }

    @Test
    void source() throws URISyntaxException {
        Zip zip = Zip.of(Path.of(getClass().getResource("test.zip").toURI()));
        ByteSource src = zip.source("a/test2.txt");
        assertEquals("test 2 file contents", new String(src.asBytes()));
    }

    @Test
    void source_Invalid() throws URISyntaxException {
        Zip zip = Zip.of(Path.of(getClass().getResource("test.zip").toURI()));
        assertThrows(RuntimeException.class, () -> zip.source("no-such-file.txt"));
    }

    @Test
    void source_folder_Invalid() throws URISyntaxException {
        Zip zip = Zip.of(Path.of(getClass().getResource("test.zip").toURI()));
        assertThrows(RuntimeException.class, () -> zip.source("a/"));
    }

    @Test
    void sources() throws URISyntaxException {
        Zip zip = Zip.of(Path.of(getClass().getResource("test.zip").toURI()));
        ByteSources srcs = zip.sources();
        assertNotNull(srcs);

        Map<String, String> texts = new HashMap<>();
        srcs.processStreams((n, st) -> {
            try {
                return texts.put(n, new String(st.readAllBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // directories must be skipped
        assertEquals(Map.of(
                "test.txt", "test file contents",
                "a/test3.txt", "test 3 file contents",
                "a/test2.txt", "test 2 file contents",
                "b/c/test4.txt", "test 4 file contents"), texts);

    }
}
