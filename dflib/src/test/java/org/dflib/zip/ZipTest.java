package org.dflib.zip;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;

public class ZipTest {

    static List<Zip> zips() throws URISyntaxException {
        return List.of(
                Zip.of(ByteSource.ofUrl(ZipTest.class.getResource("test.zip"))),
                Zip.of(Path.of(ZipTest.class.getResource("test.zip").toURI()))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "zips")
    void list(Zip zip) {
        List<String> names = zip.list().stream().map(ZipEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("a/", "a/test2.txt", "a/test3.txt", "b/", "b/c/", "b/c/test4.txt", "test.txt"), names);
    }


    @ParameterizedTest
    @MethodSource(value = "zips")
    void source(Zip zip) {
        ByteSource src = zip.source("a/test2.txt");
        assertEquals("a/test2.txt", src.uri().orElse(null));
        assertEquals("test 2 file contents", new String(src.asBytes()));
    }

    @ParameterizedTest
    @MethodSource(value = "zips")
    void source_Invalid(Zip zip) {
        assertThrows(RuntimeException.class, () -> zip.source("no-such-file.txt").asBytes());
    }

    @ParameterizedTest
    @MethodSource(value = "zips")
    void source_folder_Invalid(Zip zip) {
        assertThrows(RuntimeException.class, () -> zip.source("a/").asBytes());
    }

    @ParameterizedTest
    @MethodSource(value = "zips")
    void sources(Zip zip) {
        ByteSources srcs = zip.sources();
        assertNotNull(srcs);

        // capturing both source names and contents
        Map<String, String> labeledTexts = srcs.process((n, s) -> s.uri().orElse("") + ":" + new String(s.asBytes()));

        // directories must be skipped
        assertEquals(Map.of(
                "test.txt", "test.txt:test file contents",
                "a/test3.txt", "a/test3.txt:test 3 file contents",
                "a/test2.txt", "a/test2.txt:test 2 file contents",
                "b/c/test4.txt", "b/c/test4.txt:test 4 file contents"), labeledTexts);

    }
}
