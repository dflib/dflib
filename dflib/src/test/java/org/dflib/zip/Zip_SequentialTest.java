package org.dflib.zip;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;

public class Zip_SequentialTest {

    @Test
    void of() {
        Zip zip = Zip.of(ByteSource.ofUrl(getClass().getResource("test.zip")));

        // TODO: this will work until DFLib becomes smart enough to distinguish file-based URLs
        //  then we'll need to switch to something like a web URL to produce a sequential access source
        assertTrue(zip instanceof Zip.SequentialZip);
    }

    @Test
    void list() {
        Zip zip = Zip.of(ByteSource.ofUrl(getClass().getResource("test.zip")));
        List<String> names = zip.list().stream().map(ZipEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("a/", "a/test2.txt", "a/test3.txt", "b/", "b/c/", "b/c/test4.txt", "test.txt"), names);
    }

    @Test
    void source() {
        Zip zip = Zip.of(ByteSource.ofUrl(getClass().getResource("test.zip")));
        ByteSource src = zip.source("a/test2.txt");
        assertEquals("test 2 file contents", new String(src.asBytes()));
    }

    @Test
    void source_Invalid() {
        Zip zip = Zip.of(ByteSource.ofUrl(getClass().getResource("test.zip")));
        assertThrows(RuntimeException.class, () -> zip.source("no-such-file.txt").asBytes());
    }

    @Test
    void source_folder_Invalid() {
        Zip zip = Zip.of(ByteSource.ofUrl(getClass().getResource("test.zip")));
        assertThrows(RuntimeException.class, () -> zip.source("a/").asBytes());
    }

    @Test
    void sources() {
        Zip zip = Zip.of(ByteSource.ofUrl(getClass().getResource("test.zip")));
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
