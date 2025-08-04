package org.dflib;

import org.dflib.codec.Codec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ByteSourceTest {

    @Test
    public void of_bytes() {
        byte[] b = "abcd".getBytes();
        assertArrayEquals(b, ByteSource.of(b).asBytes());
    }

    @Test
    public void ofPath() throws URISyntaxException {
        Path p = Path.of(getClass().getResource("test.txt").toURI());
        assertTrue(Files.isRegularFile(p));

        ByteSource s = ByteSource.ofPath(p);
        assertEquals(p.toAbsolutePath().toString(), s.uri().orElse(null));

        assertArrayEquals("test text".getBytes(), s.asBytes());
    }

    @Test
    public void ofFile() throws URISyntaxException {
        File f = new File(getClass().getResource("test.txt").toURI());
        ByteSource s = ByteSource.ofFile(f);
        assertEquals(f.getAbsolutePath(), s.uri().orElse(null));
        assertArrayEquals("test text".getBytes(), s.asBytes());
    }

    @Test
    public void ofFileName() throws URISyntaxException {
        String f = new File(getClass().getResource("test.txt").toURI()).getAbsoluteFile().getPath();
        ByteSource s = ByteSource.ofFile(f);
        assertEquals(f, s.uri().orElse(null));
        assertArrayEquals("test text".getBytes(), s.asBytes());
    }

    @Test
    public void ofUrl() {
        ByteSource s = ByteSource.ofUrl(getClass().getResource("test.txt"));
        assertEquals(getClass().getResource("test.txt").toString(), s.uri().orElse(null));
        assertArrayEquals("test text".getBytes(), s.asBytes());
    }

    @Test
    public void ofUrlString() {
        ByteSource s = ByteSource.ofUrl(getClass().getResource("test.txt").toString());
        assertEquals(getClass().getResource("test.txt").toString(), s.uri().orElse(null));
        assertArrayEquals("test text".getBytes(), s.asBytes());
    }

    @Test
    public void unzip() {
        ByteSources srcs = ByteSource.ofUrl(getClass().getResource("zip/test1.zip")).unzip();

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

    @ParameterizedTest
    @ValueSource(strings = {"tar/test1.tar", "tar/test1.tar.gz"})
    public void untar(String fileName) {
        ByteSources srcs = ByteSource.ofUrl(getClass().getResource(fileName)).untar();

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

    @Test
    public void decompress() {
        ByteSource src = ByteSource.ofUrl(getClass().getResource("compressed.txt.gz")).decompress(Codec.GZIP);
        assertEquals("will be decompressed", new String(src.asBytes()));
    }
}
